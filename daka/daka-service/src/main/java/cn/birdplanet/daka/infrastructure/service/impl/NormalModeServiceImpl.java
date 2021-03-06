package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.dto.NormalModeDTO;
import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.NormalModeRound;
import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.daka.infrastructure.persistence.punch.NormalModeMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.NormalModeOrderMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.NormalModeRoundMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.NormalModeTemplateMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IBrokerageService;
import cn.birdplanet.daka.infrastructure.service.INormalModeService;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.RedPackageForWx;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.BonusMethodCodes;
import cn.birdplanet.toolkit.extra.code.DayMethodCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author ??????[uncle.yang@outlook.com]
 * @title: NormalModeActivityServiceImpl
 * @description: ?????????????????????
 * @date 2019/10/11 00:47
 */
@Slf4j
@Service
public class NormalModeServiceImpl extends BaseService implements INormalModeService {

  @Autowired private IUserService userService;
  @Autowired private INoticeService noticeService;
  @Autowired private IBalanceService balanceService;
  @Autowired private IWalletService walletService;
  @Autowired private IBrokerageService brokerageService;

  @Autowired private UserMapper userMapper;
  @Autowired private NormalModeMapper activityMapper;
  @Autowired private NormalModeOrderMapper orderMapper;
  @Autowired private NormalModeRoundMapper roundMapper;
  @Autowired private NormalModeTemplateMapper templateMapper;

  @Override public List<NormalModeTemplate> getAvailableTemplate() {
    Example example = new Example(NormalModeTemplate.class);
    example.createCriteria().andEqualTo("status", "Y");
    List<NormalModeTemplate> templates = templateMapper.selectByExample(example);
    return templates;
  }

  private LocalTime changeLocaltime(final boolean flag, final LocalTime localTime) {
    if (BirdplanetConstants.ZERO_TIME != localTime) {
      long rand = (long) (1 + Math.random() * 10);
      LocalTime temp = flag ? localTime.plusMinutes(rand) : localTime.minusMinutes(rand);
      return temp;
    }
    return localTime;
  }

  @Override public synchronized boolean generateNextPeriodActivityWithTemplate() {
    LocalDate now = LocalDate.now();
    List<NormalModeTemplate> templates = this.getAvailableTemplate();
    if (!templates.isEmpty()) {
      templates.forEach(template -> {
        // ????????????, ???????????????????????? ??????????????????
        int intervalDays = template.getCycleDays() + ((null == template.getLastGeneratePeriod()) ? 0
            : template.getIntervalDays());
        // ????????????
        LocalDate period = now.plusDays(intervalDays);
        // ?????????????????????????????????????????????
        NormalMode record = new NormalMode(period, template);

        // ??????????????????????????????
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getIsTimeChange())) {
          // ????????????
          boolean flag = YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getTimeChangeLast());
          record.setPunchStartAt1(this.changeLocaltime(flag, template.getPunchStartAt1()));
          record.setPunchEndAt1(this.changeLocaltime(flag, template.getPunchEndAt1()));
          record.setPunchStartAt2(this.changeLocaltime(flag, template.getPunchStartAt2()));
          record.setPunchEndAt2(this.changeLocaltime(flag, template.getPunchEndAt2()));
          record.setPunchStartAt3(this.changeLocaltime(flag, template.getPunchStartAt3()));
          record.setPunchEndAt3(this.changeLocaltime(flag, template.getPunchEndAt3()));
          // ?????????
          templateMapper.updateLastTimeChangeById(template.getId(),
              flag ? YesOrNoCodes.NO.getCode() : YesOrNoCodes.YES.getCode());
        }

        // ??????????????????
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getIsAutoGenerate())) {
          if (null != template.getLastGeneratePeriod() &&
              period.isEqual(template.getLastGeneratePeriod())) {
            log.debug("{}??????????????????????????????????????????????????????!!!", period);
            return;
          }
          // ????????????????????????
          if (null == template.getLastGeneratePeriod()) { // ??????
            activityMapper.insertSelective(record);
          } else {
            // ?????????????????????????????????????????????????????????
            LocalDate lastGeneratePeriod = period.minusDays(intervalDays);
            if (lastGeneratePeriod.equals(template.getLastGeneratePeriod())) {
              activityMapper.insertSelective(record);
            } else {
              return;
            }
          }
        } else { // ?????????????????????????????????????????????????????????????????????
          activityMapper.insertSelective(record);
          templateMapper.delById(template.getId());
        }
        // ????????????????????????
        templateMapper.updateLastPeriodById(template.getId(), period);
      });
    }
    return true;
  }

  @Override public List<NormalMode> getAllPunchingActivities() {
    Example example = new Example(NormalMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", ActivityStatusCodes._punching.getCode());
    example.orderBy("id").desc();
    return activityMapper.selectByExample(example);
  }

  @Override public List<NormalMode> getAllEndOrPunchingAndNotSettledActivities() {
    Example example = new Example(NormalMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andCondition("(status ="
        + ActivityStatusCodes._end.getCode()
        + " or status ="
        + ActivityStatusCodes._punching.getCode()
        + ")")
        .andEqualTo("isSettled", YesOrNoCodes.NO.getCode());
    example.orderBy("id").desc();
    return activityMapper.selectByExample(example);
  }

  @Override public List<NormalMode> getAllEndAndNotSettledActivities() {
    Example example = new Example(NormalMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", ActivityStatusCodes._end.getCode())
        .andEqualTo("isSettled", YesOrNoCodes.NO.getCode());
    example.orderBy("id").desc();
    return activityMapper.selectByExample(example);
  }

  @Override
  public List<NormalMode> getAllActivitiesByPage(int pageNum, int pageSize) {
    return this.getAllActivitiesByPage(null, pageNum, pageSize);
  }

  @Override
  public List<NormalMode> getAllActivitiesByPage(ActivityStatusCodes statusCodes, int pageNum,
      int pageSize) {
    Example example = new Example(NormalMode.class);
    Example.Criteria criteria = example.createCriteria();
    if (null != statusCodes) {
      criteria.andEqualTo("status", statusCodes.getCode());
      //
      if (ActivityStatusCodes.PLAZA.getCode() == statusCodes.getCode()) {
        criteria.andCondition("start_datetime > now()");
      }
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return activityMapper.selectByExample(example);
  }

  @Override public List<NormalModeTemplate> getAllTemplateByPage(int pageNum, int pageSize) {
    Example example = new Example(NormalModeTemplate.class);
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return templateMapper.selectByExample(example);
  }

  @Override
  public List<NormalModeOrder> getOrdersWithPage(long uid, int status, int pageNum,
      int pageSize) {
    Example example = new Example(NormalModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    if (status != 0) {
      criteria.andEqualTo("status", status);
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return orderMapper.selectByExample(example);
  }

  @Override
  public List<NormalModeOrder> getOrdersForJoinedWithPage(long uid, int status, int pageNum,
      int pageSize) {
    return this.getOrdersWithPage(uid, status, pageNum, pageSize);
  }

  @Override
  public List<NormalModeOrder> getOrdersForPunching(long uid) {
    return orderMapper.getOrdersForPunching(uid);
  }

  @Override
  public List<NormalModeOrder> getOrdersForRegistered(long uid) {
    return orderMapper.getOrdersForRegistered(uid);
  }

  @Override
  public List<NormalMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize) {
    return this.getAllActivitiesByPage(ActivityStatusCodes.PLAZA, pageNum, pageSize);
  }

  @Override public PageInfo<NormalModeDTO> getPlazaActivities() {
    String rkey = this.getPlazaRKEY();
    // ??????????????????????????????????????????????????????????????????????????????????????????????????????
    PageInfo pageInfo = (PageInfo) redisUtils.get(rkey);
    if (null != pageInfo) {
      return pageInfo;
    }
    List<NormalMode> data = this.getActivitiesForPlazaWithPage(1, 100);
    List<NormalModeDTO> dtos = Lists.newArrayListWithExpectedSize(data.size());
    NormalModeDTO dto;
    for (NormalMode record : data) {
      dto = this.normalMode2Dto(record);
      dtos.add(dto);
    }
    pageInfo = new PageInfo(data);
    pageInfo.setList(dtos);
    //
    redisUtils.set1Day(rkey, pageInfo);
    return pageInfo;
  }

  @Override public String getPlazaRKEY() {
    return RedisConstants.NORMAL_MODE_PLAZA_KEY_PREFIX + PunchUtils.punchPeriod(
        LocalDate.now());
  }

  @Override public NormalMode getActivityById(long id) {
    return activityMapper.selectByPrimaryKey(id);
  }

  @Override public NormalMode getActivityByIdFromRedis(long id) {
    NormalMode activity = (NormalMode) redisUtils.get(this.getRKeyForActivity(id));
    if (null == activity) {
      activity = this.getActivityById(id);
      redisUtils.set1Month(this.getRKeyForActivity(id), activity);
    }
    return activity;
  }

  @Override public NormalModeOrder getOrderByActivityId(long aid, long uid) {
    Example example = new Example(NormalModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid).andEqualTo("uid", uid);
    return orderMapper.selectOneByExample(example);
  }

  @Override public List<NormalModeOrder> getOrdersByActivityId(long aid) {
    Example example = new Example(NormalModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid);
    return orderMapper.selectByExample(example);
  }

  @Override public List<NormalModeOrder> getNoFailOrdersByActivityId(long aid) {
    Example example = new Example(NormalModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid)
        .andNotEqualTo("status", PunchStatusCodes.fail.getCode());
    return orderMapper.selectByExample(example);
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean join(NormalMode activity, User user, long id,
      String period, BigDecimal totalAmount, int multiple) {
    NormalModeOrder record = this.getOrderByActivityId(id, user.getUid());
    if (record != null) {
      return false;
    }
    // ????????????
    boolean flag = userMapper.updateBalanceSubtract(user.getUid(), totalAmount) == 1;
    if (!flag) {
      throw new RuntimeException(
          "??????N???????????????????????? >>> uid:" + user.getUid() + ", amount:" + totalAmount);
    }
    // ??????????????????
    NormalModeOrder order = new NormalModeOrder();
    order.setUid(user.getUid());
    order.setActivityId(activity.getId());
    order.setPeriod(activity.getPeriod());
    order.setAmount(totalAmount);
    order.setMultiple(multiple);
    order.setStatus(PunchStatusCodes.joining.getCode());
    order.setCreatedAt(LocalDateTime.now());
    orderMapper.insertSelective(order);

    // ?????? ????????????
    balanceService.addBalanceDtl(new BalanceDtl(user.getUid(), "-" + totalAmount.intValue(),
        "?????????" + activity.getTitle() + "?????????"), BalanceDtlTypeCodes._join);

    //  ?????????????????????????????? ++
    activityMapper.changeNumberForJoin(activity.getId(), totalAmount);

    activity.setTotalAmount(activity.getTotalAmount().add(totalAmount));
    activity.setTotalPeople(activity.getTotalPeople() + 1);

    // ????????????
    taskExecutor.execute(() -> {
      // ??????redis????????????
      redisUtils.set1Month(this.getRKeyForActivity(activity.getId()), activity);
      // ??????redis?????????????????????
      userService.changeUserCache(user.getUid());
      // ???????????????, ????????????????????????redis
      redisUtils.hash_put(this.getRKeyForJoinedUsers(activity.getId()), user.getUid(),
          new ActivityUserVo(user.getUid(), user.getNickName(), user.getAvatarPath(),
              PunchStatusCodes.joining.getCode()),30, TimeUnit.DAYS);
    });
    return flag;
  }

  @Override public int updateStatusForActivityExpired(LocalDateTime endTime) {
    return activityMapper.UpdateStatusForActivityEnd(endTime);
  }

  @Override public int updateStatusForActivityStart(LocalDateTime startTime) {
    return activityMapper.updateStatusForActivityStart(startTime);
  }

  /**
   * ????????? normal_mode:{period}:{template}:{id}
   *
   * @param aid ????????????
   * @return redis key
   */
  @Override public String getRKeyForActivity(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.NORMAL_MODE_KEY_PREFIX);
    key.append(aid);
    return key.toString();
  }

  @Override public String getRKeyForJoinedUsers(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.NORMAL_MODE_KEY_PREFIX);
    key.append(aid).append(":joined");
    return key.toString();
  }

  public String getRKeyForOrders(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.NORMAL_MODE_KEY_PREFIX);
    key.append(aid).append(":order");
    return key.toString();
  }

  @Override public NormalModeDTO normalMode2Dto(NormalMode record) {
    NormalModeDTO dto = DozerMapperUtil.map(record, NormalModeDTO.class);
    dto.setPunchStartAt1(record.getPunchStartAt1().format(DateTimeFormatter.ofPattern("HH:mm")));
    dto.setPunchEndAt1(record.getPunchEndAt1().format(DateTimeFormatter.ofPattern("HH:mm")));

    dto.setPunchStartAt2(record.getPunchStartAt2().format(DateTimeFormatter.ofPattern("HH:mm")));
    dto.setPunchEndAt2(record.getPunchEndAt2().format(DateTimeFormatter.ofPattern("HH:mm")));

    dto.setPunchStartAt3(record.getPunchStartAt3().format(DateTimeFormatter.ofPattern("HH:mm")));
    dto.setPunchEndAt3(record.getPunchEndAt3().format(DateTimeFormatter.ofPattern("HH:mm")));

    // ??????????????????
    dto.setTotalAmount(record.getTotalAmount()
        .add(null == record.getDummyTotalAmount() ? BirdplanetConstants.ZERO_BD
            : record.getDummyTotalAmount()));
    dto.setTotalPeople(record.getTotalPeople() + record.getDummyTotalPeople());

    return dto;
  }

  @Override public List<ActivityUserVo> getJoinedUsersByActivityId(long aid) {
    String rkey = this.getRKeyForJoinedUsers(aid);
    // ??????????????? ???????????????????????????????????????
    if (redisUtils.hash_len(rkey) < 1) {
      List<ActivityUserVo> orders = orderMapper.getJoinedUsersByActivityId(aid);
      Map dataMap = Maps.newHashMapWithExpectedSize(orders.size());
      for (ActivityUserVo vo : orders) {
        dataMap.put(vo.getUid(), vo);
      }
      redisUtils.hash_putAll(rkey, dataMap,365, TimeUnit.DAYS);
      return orders;
    } else {
      return (List<ActivityUserVo>) redisUtils.hash_values(rkey);
    }
  }

  @Override public List<NormalModeRound> getRounds(long oid, Long uid) {
    Example example = new Example(NormalModeRound.class);
    Example.Criteria criteria = example.createCriteria();
    if (null != uid) {
      criteria.andEqualTo("uid", uid);
    }
    criteria.andEqualTo("orderId", oid);
    return roundMapper.selectByExample(example);
  }

  @Override public NormalModeOrder getOrderById(long oid, long uid) {
    NormalModeOrder order = orderMapper.selectByPrimaryKey(oid);
    return null != order ? (order.getUid() == uid ? order : null) : null;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public synchronized String checkin(final long uid, final NormalModeOrder order,
      final LocalDateTime punchAt) {
    // ????????????
    if (PunchStatusCodes.fail.getCode() == order.getStatus()) {
      return "false";
    }
    // ?????????????????????
    NormalModeRound round = new NormalModeRound(order, punchAt);
    // ?????????????????????
    NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
    // ??????????????????
    if (activity.getStartDatetime().isAfter(LocalDateTime.now())) {
      return "notStart";
    }
    // ???????????????????????????
    int currTimes = order.getTimes() % activity.getTimesOneday();
    currTimes = currTimes + 1;
    // ???????????????????????????
    round.setRound(currTimes);
    log.debug("???????????????{}?????????::{}", currTimes, order);
    // ????????????????????? :: 2/1
    int dayInCycle = (int) Math.ceil(order.getTimes() / activity.getTimesOneday());
    dayInCycle = dayInCycle + 1;
    log.debug("???????????????{}???::{}", dayInCycle, order);
    // ?????????????????????
    round.setDayInCycle(dayInCycle);

    LocalDate nowDate = punchAt.toLocalDate();
    LocalDateTime startAt = null, endAt = null;
    if (currTimes == 1) { // ????????????
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt1()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt1()));
    } else if (currTimes == 2) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt2()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt2()));
    } else if (currTimes == 3) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt3()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt3()));
    } else {
      log.error(
          "??????????????????????????????: activity:{}, order:{}, round:{}, startAt:{}, endAt:{}",
          activity, order, round, startAt, endAt);
      throw new RuntimeException("??????????????????????????????");
    }
    // ???????????????
    round.setStartTime(startAt);
    round.setEndTime(endAt);
    //
    if (punchAt.isBefore(startAt)) {
      return "notAt";
    }
    try {
      // ??????????????????
      if (punchAt.isAfter(startAt) && punchAt.isBefore(endAt)) {
        // ?????????????????????????????????????????????????????????????????????
        NormalModeRound roundDB =
            this.getRound(order.getUid(), round.getOrderId(), round.getDayInCycle(),
                round.getRound());
        // ???????????????
        if (null != roundDB) {
          return roundDB.getStatus() == PunchStatusCodes.success.getCode() ? "true" : "false";
        }
        round.setStatus(PunchStatusCodes.success.getCode());
        // ??????????????????
        roundMapper.insertSelective(round);
        // ?????? ??????????????????
        this.addTimes(order.getId());
        // ????????????::????????????
        noticeService.addCheckinResultNoticeForNormalMode(uid, activity.getPeriod(),
            activity.getTitle(), true, PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
        // ?????????????????????????????? :: ?????????????????????????????????????????????????????????????????????
        int timesNeed = activity.getTimesOneday() * activity.getCycleDays();
        int timesPunch = order.getTimes() + 1;
        if (timesNeed == timesPunch) {
          orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.success.getCode());
        }
        return "true";
      } else if (punchAt.isAfter(endAt)) {
        // ????????????, ???????????????????????????
        orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.fail.getCode());
        activityMapper.updatePunchFail(order.getActivityId(), order.getAmount());
        // ????????????
        noticeService.addPunchResultNotice(uid, activity.getPeriod(), currTimes, false,
            PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
        // ???????????? > ??????????????????
        redisUtils.del(this.getRKeyForActivity(activity.getId()));
        return "false";
      }
    } catch (Exception e) {
      log.error("??????????????????????????????????????????????????????", e);
      throw new RuntimeException("???????????????????????????????????????");
    }
    return "error";
  }

  private NormalModeRound getRound(long uid, long orderId, int dayInCycle, int round) {
    Example example = new Example(NormalModeRound.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("uid", uid)
        .andEqualTo("orderId", orderId)
        .andEqualTo("dayInCycle", dayInCycle)
        .andEqualTo("round", round);
    return roundMapper.selectOneByExample(example);
  }

  @Override public synchronized boolean checkInPunchingTime(NormalModeOrder order) {
    LocalDateTime punchAt = LocalDateTime.now();
    // ????????????????????????????????? ??????????????????????????????????????????????????????
    // ??????????????????????????????
    if (PunchStatusCodes.joining.getCode() != order.getStatus()) {
      return false;
    }
    // ?????????????????????
    NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
    // ??????????????????
    if (activity.getStartDatetime().isAfter(punchAt)) {
      return false;
    }
    // ???????????????????????????
    int currTimes = order.getTimes() % activity.getTimesOneday();
    currTimes = currTimes + 1;
    LocalDate nowDate = punchAt.toLocalDate();
    LocalDateTime startAt, endAt;
    if (currTimes == 1) { // ????????????
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt1()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt1()));
    } else if (currTimes == 2) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt2()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt2()));
    } else if (currTimes == 3) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt3()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt3()));
    } else {
      log.error("??????????????????????????????: activity:{}, order:{}", activity, order);
      throw new RuntimeException("??????????????????????????????");
    }
    // ??????????????????
    if (punchAt.isAfter(activity.getEndDatetime())) {
      log.info("????????????{}????????????????????????{}", punchAt, activity.getEndDatetime());
      this.triggerCheckInFailure(order, activity, startAt, endAt);
      return true;
    }
    //
    if (punchAt.isAfter(endAt)) {
      //  ????????????????????? ??? ??????????????? ?????????????????????
      long days = activity.getPeriod().until(punchAt.toLocalDate(), ChronoUnit.DAYS) + 1;
      if (order.getTimes() != activity.getTimesOneday() * days) {
        log.info("????????????{}??????????????????????????????{}", punchAt, endAt);
        this.triggerCheckInFailure(order, activity, startAt, endAt);
        return true;
      }
    }
    return false;
  }

  private boolean triggerCheckInFailure(NormalModeOrder order,
      NormalMode activity, LocalDateTime startAt, LocalDateTime endAt) {
    // ????????????, ???????????????????????????
    orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.fail.getCode());
    activityMapper.updatePunchFail(order.getActivityId(), order.getAmount());
    // ????????????
    noticeService.addPunchResultNoticeForNormalMode(order.getUid(), activity.getPeriod(),
        activity.getTitle(), false, PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
    // ????????????
    redisUtils.del(this.getRKeyForActivity(activity.getId()));
    return true;
  }

  @Override public List<NormalMode> getTodayEndActivity(LocalDateTime endAt) {
    Example example = new Example(NormalMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("endDatetime", endAt);
    return activityMapper.selectByExample(example);
  }

  @Override public long triggerCheckInFailureByActivity(NormalMode activity) {
    long count;
    List<NormalModeOrder> orders = this.getOrdersByActivityId(activity.getId());
    log.info("??????????????????:: aid:{}/???{}???", activity.getId(), orders.size());
    long result = 0L;
    for (NormalModeOrder order : orders) {
      log.info("aid:{} start >>>order:{}", order.getActivityId(), order);
      if (this.checkInPunchingTime(order)) {
        result++;
      }
      log.info("aid:{} end >>>oid:{}/result:{}", order.getActivityId(), order.getId(), result);
    }
    count = result;
    return count;
  }

  @Override public synchronized long settlePrincipalForActivity(NormalMode activity) {
    long count = 0;
    try {
      // ??????????????????????????????????????????
      List<NormalModeOrder> orders = this.getOrdersByActivityId(activity.getId());
      log.info("???????????????[{}]???????????????????????????{}???", activity.getId(), orders.size());
      //
      long result = 0L;
      for (int i = 0; i < orders.size(); i++) {
        if (this.settlePrincipalForOrder(orders.get(i))) {
          result++;
        }
      }
      count = result;
      log.info("[{}] ???????????????{}???", activity.getId(), count);
      // ????????? ?????????
      activityMapper.updateSettledComplete(activity.getId());
    } catch (Exception e) {
      log.error("?????????????????? activity::{} ERR :: {}", activity, e);
    }
    return count;
  }

  private boolean settlePrincipalForOrder(NormalModeOrder order) {
    try {
      NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
      // ??????????????????????????? :: ??????????????????????????????order
      if (this.checkInPunchingTime(order)) {
        order = this.getOrderById(order.getId(), order.getUid());
      }
      // ????????????????????????????????????
      if (PunchStatusCodes.success.getCode() == order.getStatus()) {
        // ????????????????????????????????????????????????????????????
        if (userMapper.updateWalletAdd(order.getUid(), order.getAmount()) == 1) {
          log.info("oid:{} uid:{} ???????????????????????????{}", order.getId(), order.getUid(), order.getAmount());
          // ??????????????????
          String content = new StringBuilder().append("???")
              .append(activity.getTitle())
              .append("???")
              .append(WalletDtlTypeCodes.PUNCH_PRINCIPAL.getDesc()).toString().intern();

          WalletDtl walletDtl =
              new WalletDtl(order.getUid(), "+" + NumberUtil.format3Str(order.getAmount()),
                  content);
          walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes.PUNCH_PRINCIPAL);
        } else {
          log.info("### oid:{} uid:{} ???????????????????????????{}", order.getId(), order.getUid(),
              order.getAmount());
        }
        return true;
      } else if (PunchStatusCodes.fail.getCode() == order.getStatus()) {
        log.info("???????????????????????????::{}", order);
      } else {
        log.debug("??????????????????????????????::?{}", order);
      }
    } catch (Exception e) {
      log.error("?????????????????? order::{} ERR :: {}", order, e);
    }
    return false;
  }

  @Override public synchronized long settleBonusForActivity(NormalMode activity) {
    log.info("settleBonus ??????:: {}", activity);
    long count = 0;
    try {
      // ??????????????????????????????????????????
      List<NormalModeOrder> orders = this.getNoFailOrdersByActivityId(activity.getId());
      log.info("settleBonus ???????????????[{}]???????????????????????????{}???", activity.getId(), orders.size());
      // ???????????? =????????????*80%???/ ????????????
      BigDecimal bonusPool = activity.getFailTotalAmount()
          .multiply(new BigDecimal("0.80"))
          .divide(new BigDecimal(activity.getCycleDays()), 3, BigDecimal.ROUND_DOWN);
      // ????????????
      int succPeople = activity.getTotalPeople() + activity.getDummyTotalPeople()
          - activity.getFailTotalPeople();
      log.info("settleBonus aid:{} ????????????::{}", activity.getId(), succPeople);
      // ???????????? ????????????
      List<BigDecimal> bonusList = Lists.newArrayList();
      RedPackageForWx.RedPackage moneyPackage = new RedPackageForWx.RedPackage();
      moneyPackage.remainMoney = bonusPool;
      moneyPackage.remainSize = succPeople < 0 ? 0 : succPeople;
      while (moneyPackage.remainSize != 0) {
        bonusList.add(RedPackageForWx.getRandomMoney(moneyPackage));
      }
      log.info("settleBonus aid:{} ????????????::{}", activity.getId(), bonusList);
      //
      long result = 0L;
      for (int i = 0, osize = orders.size(), redSize = bonusList.size() - 1; i < osize; i++) {
        //

        if (this.settleBonusForOrder(orders.get(i),
            i > redSize ? BirdplanetConstants.BD001 : bonusList.get(i))) {
          result++;
        }
      }
      count = result;
      log.info("[{}] ???????????????{}???", activity.getId(), count);
    } catch (Exception e) {
      log.error("ERR:: ?????????????????? activity::{} /{}", activity, e);
    }
    return count;
  }

  private boolean settleBonusForOrder(NormalModeOrder order, BigDecimal bonusForR) {
    try {
      if (PunchStatusCodes.fail.getCode() == order.getStatus()) {
        return false;
      }
      //
      NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
      // ???????????????????????????????????? :: ????????????????????????
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleBonusDaily())) {

      }
      //
      User user = userService.getByUidFromRedis(order.getUid());
      // ??????????????????????????? :: ??????????????????????????????order
      if (this.checkInPunchingTime(order)) {
        order = this.getOrderById(order.getId(), order.getUid());
      }
      BigDecimal bonusPool, bonus, inviterBrokerage;
      // ?????????????????????A?????????B?????????R??????
      if (BonusMethodCodes.B.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! ??????
        // ????????????????????????????????????
        bonus = DayMethodCodes.FA.getCode().equalsIgnoreCase(activity.getDayMethod())
            ? activity.getDailyFixedAmount()
            : (order.getAmount().multiply(activity.getDailyRate()));
      } else if (BonusMethodCodes.A.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! ??????
        // ????????????20% :: (????????????*80%) / ????????????
        activity = this.getActivityById(activity.getId());
        bonusPool = activity.getFailTotalAmount()
            .multiply(new BigDecimal("0.80"))
            .divide(new BigDecimal(activity.getCycleDays()), 3, BigDecimal.ROUND_DOWN);
        // ??????????????????
        int succPeople = activity.getTotalPeople() + activity.getDummyTotalPeople()
            - activity.getFailTotalPeople();
        // ???????????? = ??????????????? / ????????????
        bonus = activity.getFailTotalPeople() == 0 ? new BigDecimal("0")
            : bonusPool.divide(new BigDecimal(succPeople), 6, BigDecimal.ROUND_DOWN);
      } else if (BonusMethodCodes.R.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! ??????
        activity = this.getActivityById(activity.getId());
        // ????????????: ??????????????? / ????????????
        bonus = activity.getFailTotalPeople() == 0 ? new BigDecimal("0") : bonusForR;
      } else {
        log.error("???????????????????????????::{}", activity);
        throw new Exception("???????????????????????????:" + activity);
      }
      //
      inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
      // ???????????????????????????????????????
      if (StringUtils.isNotBlank(user.getInviterCode()) && user.getInviterUid() != null
          && !BonusMethodCodes.B.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        bonus = bonus.subtract(inviterBrokerage);
        log.info("uid:{}:: {}???????????????????????????{}", order.getUid(), bonus, inviterBrokerage);
      }
      // ?????????????????????
      log.info("uid:{} ?????????{}", order.getUid(), bonus);
      if (bonus.compareTo(BirdplanetConstants.ZERO_BD) == 1) {
        if (userMapper.updateWalletAdd(user.getUid(), bonus) == 1) {
          log.info("uid:{} ???????????????????????????{}", order.getUid(), bonus);
        } else {
          log.info("uid:{} ???????????????????????????{}", order.getUid(), bonus);
        }
      }
      // ??????????????????
      String content = new StringBuilder().append("???")
          .append(activity.getTitle())
          .append("???")
          .append(
              activity.getFailTotalPeople() != 0 ? WalletDtlTypeCodes.PUNCH_JL.getDesc() : "????????????")
          .toString()
          .intern();
      WalletDtl walletDtl =
          new WalletDtl(order.getUid(), "+" + NumberUtil.format3Str(bonus),
              content);
      walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes.PUNCH_JL);

      // ?????????????????????
      if (StringUtils.isNotBlank(user.getInviterCode()) && user.getInviterUid() != null) {
        log.info("uid:{} ??????????????? uid:{}, code:{}", order.getUid(), user.getInviterUid(),
            user.getInvitationCode());
        // ?????????????????????????????????????????????10%?????????????????????
        log.info("uid:{} ?????????????????????{}", order.getUid(), inviterBrokerage);
        //??????++ , ????????????++
        userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(), inviterBrokerage);
        // ??????????????????
        brokerageService.addWithPunch(user.getInviterUid(), user.getUid(), user.getNickName(),
            activity.getTitle(), NumberUtil.format3Str(inviterBrokerage));
      }
      return true;
    } catch (Exception e) {
      log.error("?????????????????? order::{} ERR :: {}", order, e);
      return false;
    }
  }

  private boolean addTimes(long oid) {
    return orderMapper.addTimesByOrderId(oid) == 1;
  }
}
