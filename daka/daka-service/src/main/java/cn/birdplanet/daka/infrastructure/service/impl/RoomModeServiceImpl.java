package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.dto.RoomModeDTO;
import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.domain.po.RoomModeOrder;
import cn.birdplanet.daka.domain.po.RoomModeRound;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.daka.infrastructure.persistence.punch.RoomModeMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.RoomModeOrderMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.RoomModeRoundMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IBrokerageService;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import cn.birdplanet.daka.infrastructure.service.IRoomModeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author ??????[uncle.yang@outlook.com]
 * @title: v
 * @description: ?????????????????????
 * @date 2019/10/11 00:47
 */
@Slf4j
@Service
public class RoomModeServiceImpl extends BaseService implements IRoomModeService {

  @Autowired private IUserService userService;
  @Autowired private INoticeService noticeService;
  @Autowired private IBalanceService balanceService;
  @Autowired private IWalletService walletService;
  @Autowired private IBrokerageService brokerageService;

  @Autowired private UserMapper userMapper;
  @Autowired private RoomModeMapper activityMapper;
  @Autowired private RoomModeOrderMapper orderMapper;
  @Autowired private RoomModeRoundMapper roundMapper;

  private LocalTime changeLocaltime(final boolean flag, final LocalTime localTime) {
    if (BirdplanetConstants.ZERO_TIME != localTime) {
      long rand = (long) (1 + Math.random() * 10);
      LocalTime temp = flag ? localTime.plusMinutes(rand) : localTime.minusMinutes(rand);
      return temp;
    }
    return localTime;
  }

  @Override public List<RoomMode> getAllPunchingActivities() {
    Example example = new Example(RoomMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", ActivityStatusCodes._punching.getCode());
    example.orderBy("id").desc();
    return activityMapper.selectByExample(example);
  }

  @Override public List<RoomMode> getAllEndOrPunchingAndNotSettledActivities() {
    Example example = new Example(RoomMode.class);
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

  @Override public List<RoomMode> getAllEndAndNotSettledActivities() {
    Example example = new Example(RoomMode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", ActivityStatusCodes._end.getCode())
        .andEqualTo("isSettled", YesOrNoCodes.NO.getCode());
    example.orderBy("id").desc();
    return activityMapper.selectByExample(example);
  }

  @Override
  public List<RoomMode> getAllActivitiesByPage(int pageNum, int pageSize) {
    return this.getAllActivitiesByPage(null, pageNum, pageSize);
  }

  @Override
  public List<RoomMode> getAllActivitiesByPage(ActivityStatusCodes statusCodes, int pageNum,
      int pageSize) {
    Example example = new Example(RoomMode.class);
    Example.Criteria criteria = example.createCriteria();
    if (null != statusCodes) {
      // ??????????????????????????????????????????
      if (ActivityStatusCodes.PLAZA.getCode() == statusCodes.getCode()) {
        criteria.andCondition("status not in (0,4)");
        //criteria.andCondition("start_datetime > now()");
      }else{
        criteria.andEqualTo("status", statusCodes.getCode());
      }
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return activityMapper.selectByExample(example);
  }

  @Override
  public List<RoomModeOrder> getOrdersWithPage(long uid, int status, int pageNum,
      int pageSize) {
    Example example = new Example(RoomModeOrder.class);
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
  public List<RoomModeOrder> getOrdersForJoinedWithPage(long uid, int status, int pageNum,
      int pageSize) {
    return this.getOrdersWithPage(uid, status, pageNum, pageSize);
  }

  @Override
  public List<RoomModeOrder> getOrdersForPunching(long uid) {
    return orderMapper.getOrdersForPunching(uid);
  }

  @Override
  public List<RoomModeOrder> getOrdersForRegistered(long uid) {
    return orderMapper.getOrdersForRegistered(uid);
  }

  @Override
  public List<RoomMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize) {
    return this.getAllActivitiesByPage(ActivityStatusCodes.PLAZA, pageNum, pageSize);
  }

  @Override public PageInfo<RoomModeDTO> getPlazaActivities() {
    String rkey = RedisConstants.ROOM_MODE_PLAZA_KEY;
    // ??????????????????????????????????????????????????????????????????????????????????????????????????????
    PageInfo pageInfo = (PageInfo) redisUtils.get(rkey);
    if (null != pageInfo) {
      return pageInfo;
    }
    List<RoomMode> data = this.getActivitiesForPlazaWithPage(1, 100);
    List<RoomModeDTO> dtos = Lists.newArrayListWithExpectedSize(data.size());
    RoomModeDTO dto;
    for (RoomMode record : data) {
      dto = this.roomMode2Dto(record);
      dtos.add(dto);
    }
    pageInfo = new PageInfo(data);
    pageInfo.setList(dtos);
    //
    redisUtils.set1Day(rkey, pageInfo);
    return pageInfo;
  }

  @Override public RoomMode getActivityById(long id) {
    return activityMapper.selectByPrimaryKey(id);
  }

  @Override public RoomMode getActivityByIdFromRedis(long id) {
    RoomMode activity = (RoomMode) redisUtils.get(this.getRKeyForActivity(id));
    if (null == activity) {
      activity = this.getActivityById(id);
      redisUtils.set1Month(this.getRKeyForActivity(id), activity);
    }
    return activity;
  }

  @Override public RoomModeOrder getOrderByActivityId(long aid, long uid) {
    Example example = new Example(RoomModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid).andEqualTo("uid", uid);
    return orderMapper.selectOneByExample(example);
  }

  @Override public List<RoomModeOrder> getOrdersByActivityId(long aid) {
    Example example = new Example(RoomModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid);
    return orderMapper.selectByExample(example);
  }

  @Override public List<RoomModeOrder> getNoFailOrdersByActivityId(long aid) {
    Example example = new Example(RoomModeOrder.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("activityId", aid)
        .andNotEqualTo("status", PunchStatusCodes.fail.getCode());
    return orderMapper.selectByExample(example);
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean join(RoomMode activity, User user, long id,
      String period, BigDecimal totalAmount, int multiple) {
    RoomModeOrder record = this.getOrderByActivityId(id, user.getUid());
    if (record != null) {
      return false;
    }
    // ????????????
    boolean flag = userMapper.updateBalanceSubtract(user.getUid(), totalAmount) == 1;
    if (!flag) {
      throw new RuntimeException(
          "??????RM???????????????????????? >>> uid:" + user.getUid() + ", amount:" + totalAmount);
    }
    // ??????????????????
    RoomModeOrder order = new RoomModeOrder();
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

    // ??????redis????????????
    redisUtils.set1Month(this.getRKeyForActivity(activity.getId()), activity);
    // ??????redis?????????????????????
    userService.changeUserCache(user.getUid());
    // ???????????????, ????????????????????????redis
    redisUtils.hash_put(this.getRKeyForJoinedUsers(activity.getId()), user.getUid(),
        new ActivityUserVo(user.getUid(), user.getNickName(), user.getAvatarPath(),
            PunchStatusCodes.joining.getCode()), 30, TimeUnit.DAYS);
    return flag;
  }

  @Override public int updateStatusForActivityExpired(LocalDateTime endTime) {
    return activityMapper.updateStatusForActivityEnd(endTime);
  }

  @Override public int updateStatusForActivityStart(LocalDateTime startTime) {
    return activityMapper.updateStatusForActivityStart(startTime);
  }

  /**
   * ????????? room_mode:{period}:{template}:{id}
   *
   * @param aid ????????????
   * @return redis key
   */
  @Override public String getRKeyForActivity(long aid) {
    String key = (RedisConstants.ROOM_MODE_ID_KEY_PREFIX + aid).intern();
    return key;
  }

  @Override public String getRKeyForJoinedUsers(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.ROOM_MODE_KEY_PREFIX);
    key.append(aid).append(":joined");
    return key.toString();
  }

  public String getRKeyForOrders(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.ROOM_MODE_KEY_PREFIX);
    key.append(aid).append(":order");
    return key.toString();
  }

  @Override public RoomModeDTO roomMode2Dto(RoomMode record) {
    RoomModeDTO dto = DozerMapperUtil.map(record, RoomModeDTO.class);
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

    //
    User user = userService.getByUidFromRedis(record.getOwnerUid());
    dto.setOwnerAvatarPath(user.getAvatarPath());
    dto.setOwnerNickName(user.getNickName());
    return dto;
  }

  @Override public List<ActivityUserVo> getJoinedUsersByActivityId(long aid) {
    String rkey = this.getRKeyForJoinedUsers(aid);
    // ??????????????? ???????????????????????????????????????
    if (redisUtils.hash_len(rkey) < 1) {
      //
      List<ActivityUserVo> orders = orderMapper.getJoinedUsersByActivityId(aid);
      // ?????????
      RoomMode activity = this.getActivityByIdFromRedis(aid);
      if (activity.getDummyTotalPeople() > 0) {
        ActivityUserVo vo;
        for (Integer dummyTotalPeople = activity.getDummyTotalPeople(); dummyTotalPeople > 0;
            dummyTotalPeople--) {
          vo = new ActivityUserVo(RandomUtils.nextLong(100, 900), "???????????????");
          orders.add(vo);
        }
        // ???????????? ??????????????????????????????
        Collections.shuffle(orders);
      }
      Map dataMap = Maps.newHashMapWithExpectedSize(orders.size());
      for (ActivityUserVo vo : orders) {
        dataMap.put(vo.getUid(), vo);
      }

      redisUtils.hash_putAll(rkey, dataMap, 365, TimeUnit.DAYS);
      return orders;
    } else {
      return (List<ActivityUserVo>) redisUtils.hash_values(rkey);
    }
  }

  @Override public List<RoomModeRound> getRounds(long oid, Long uid) {
    Example example = new Example(RoomModeRound.class);
    Example.Criteria criteria = example.createCriteria();
    if (null != uid) {
      criteria.andEqualTo("uid", uid);
    }
    criteria.andEqualTo("orderId", oid);
    return roundMapper.selectByExample(example);
  }

  @Override public RoomModeOrder getOrderById(long oid, long uid) {
    RoomModeOrder order = orderMapper.selectByPrimaryKey(oid);
    return null != order ? (order.getUid() == uid ? order : null) : null;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public synchronized String checkin(final long uid, final RoomModeOrder order,
      final LocalDateTime punchAt) {
    // ????????????
    if (PunchStatusCodes.fail.getCode() == order.getStatus()) {
      return "false";
    }
    // ?????????????????????
    RoomModeRound round = new RoomModeRound(order, punchAt);
    // ?????????????????????
    RoomMode activity = this.getActivityByIdFromRedis(order.getActivityId());
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
        RoomModeRound roundDB =
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
        noticeService.addCheckinResultNoticeForRoomMode(uid, activity.getPeriod(),
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

  private RoomModeRound getRound(long uid, long orderId, int dayInCycle, int round) {
    Example example = new Example(RoomModeRound.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("uid", uid)
        .andEqualTo("orderId", orderId)
        .andEqualTo("dayInCycle", dayInCycle)
        .andEqualTo("round", round);
    return roundMapper.selectOneByExample(example);
  }

  @Override public synchronized boolean checkInPunchingTime(RoomModeOrder order) {
    LocalDateTime punchAt = LocalDateTime.now();
    // ????????????????????????????????? ??????????????????????????????????????????????????????
    // ??????????????????????????????
    if (PunchStatusCodes.joining.getCode() != order.getStatus()) {
      return false;
    }
    // ?????????????????????
    RoomMode activity = this.getActivityByIdFromRedis(order.getActivityId());
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

  private boolean triggerCheckInFailure(RoomModeOrder order,
      RoomMode activity, LocalDateTime startAt, LocalDateTime endAt) {
    // ????????????, ???????????????????????????
    orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.fail.getCode());
    activityMapper.updatePunchFail(order.getActivityId(), order.getAmount());
    // ????????????
    noticeService.addPunchResultNoticeForRoomMode(order.getUid(), activity.getPeriod(),
        activity.getTitle(), false, PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
    // ????????????
    redisUtils.del(this.getRKeyForActivity(activity.getId()));
    return true;
  }

  private boolean addTimes(long oid) {
    return orderMapper.addTimesByOrderId(oid) == 1;
  }
}
