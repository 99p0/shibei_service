package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.dto.NormalModeDTO;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.NormalModeRound;
import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.schedulerx.persistence.punch.NormalModeMapper;
import cn.birdplanet.schedulerx.persistence.punch.NormalModeOrderMapper;
import cn.birdplanet.schedulerx.persistence.punch.NormalModeRoundMapper;
import cn.birdplanet.schedulerx.persistence.punch.NormalModeTemplateMapper;
import cn.birdplanet.schedulerx.persistence.punch.UserMapper;
import cn.birdplanet.schedulerx.service.IBalanceService;
import cn.birdplanet.schedulerx.service.IBrokerageService;
import cn.birdplanet.schedulerx.service.INormalModeService;
import cn.birdplanet.schedulerx.service.INoticeService;
import cn.birdplanet.schedulerx.service.IUserService;
import cn.birdplanet.schedulerx.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.RedPackageForWx;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: NormalModeActivityServiceImpl
 * @description: 常规模式的活动
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
        // 间隔天数, 首次的只有周期， 没有间隔天数
        int intervalDays = template.getCycleDays() + ((null == template.getLastGeneratePeriod()) ? 0
            : template.getIntervalDays());
        // 活动周期
        LocalDate period = now.plusDays(intervalDays);
        // 根据模版创建下期活动的基本信息
        NormalMode record = new NormalMode(period, template);

        // 是否需要变更打卡时间
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getIsTimeChange())) {
          // 上次打卡
          boolean flag = YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getTimeChangeLast());
          record.setPunchStartAt1(this.changeLocaltime(flag, template.getPunchStartAt1()));
          record.setPunchEndAt1(this.changeLocaltime(flag, template.getPunchEndAt1()));
          record.setPunchStartAt2(this.changeLocaltime(flag, template.getPunchStartAt2()));
          record.setPunchEndAt2(this.changeLocaltime(flag, template.getPunchEndAt2()));
          record.setPunchStartAt3(this.changeLocaltime(flag, template.getPunchStartAt3()));
          record.setPunchEndAt3(this.changeLocaltime(flag, template.getPunchEndAt3()));
          // 更新下
          templateMapper.updateLastTimeChangeById(template.getId(),
              flag ? YesOrNoCodes.NO.getCode() : YesOrNoCodes.YES.getCode());
        }

        // 周期性的活动
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(template.getIsAutoGenerate())) {
          if (null != template.getLastGeneratePeriod() &&
              period.isEqual(template.getLastGeneratePeriod())) {
            log.debug("{}的常规模式已经存在，禁止重复创建活动!!!", period);
            return;
          }
          // 根据上次生成时间
          if (null == template.getLastGeneratePeriod()) { // 首次
            activityMapper.insertSelective(record);
          } else {
            // 需要等上个周期结束之后，在生成新的周期
            LocalDate lastGeneratePeriod = period.minusDays(intervalDays);
            if (lastGeneratePeriod.equals(template.getLastGeneratePeriod())) {
              activityMapper.insertSelective(record);
            } else {
              return;
            }
          }
        } else { // 一次性的活动，生成活动后，更改此模版状态为禁用
          activityMapper.insertSelective(record);
          templateMapper.delById(template.getId());
        }
        // 更新下生成的周期
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

  @Override
  public List<NormalMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize) {
    return this.getAllActivitiesByPage(ActivityStatusCodes.PLAZA, pageNum, pageSize);
  }

  @Override public PageInfo<NormalModeDTO> getPlazaActivities() {
    String rkey = this.getPlazaRKEY();
    // 每天的可注册活动时固定的可以放入缓存中，设置过期时间，到期了自动删除
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

  @Override public int updateStatusForActivityExpired(LocalDateTime endTime) {
    return activityMapper.UpdateStatusForActivityEnd(endTime);
  }

  @Override public int updateStatusForActivityStart(LocalDateTime startTime) {
    return activityMapper.updateStatusForActivityStart(startTime);
  }

  /**
   * 示例： normal_mode:{period}:{template}:{id}
   *
   * @param aid 活动信息
   * @return redis key
   */
  @Override public String getRKeyForActivity(long aid) {
    StringBuilder key = new StringBuilder(RedisConstants.NORMAL_MODE_KEY_PREFIX);
    key.append(aid);
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

    // 虚拟人数增加
    dto.setTotalAmount(record.getTotalAmount()
        .add(null == record.getDummyTotalAmount() ? BirdplanetConstants.ZERO_BD
            : record.getDummyTotalAmount()));
    dto.setTotalPeople(record.getTotalPeople() + record.getDummyTotalPeople());

    return dto;
  }

  @Override public NormalModeOrder getOrderById(long oid, long uid) {
    NormalModeOrder order = orderMapper.selectByPrimaryKey(oid);
    return null != order ? (order.getUid() == uid ? order : null) : null;
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
    // 检查是否在签到时间内， 如果已经过了签到时间需要触发签到失败
    // 已有结果，不用再结算
    if (PunchStatusCodes.joining.getCode() != order.getStatus()) {
      return false;
    }
    // 当前的活动信息
    NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
    // 活动尚未开始
    if (activity.getStartDatetime().isAfter(punchAt)) {
      return false;
    }
    // 获取当前打卡的轮次
    int currTimes = order.getTimes() % activity.getTimesOneday();
    currTimes = currTimes + 1;
    LocalDate nowDate = punchAt.toLocalDate();
    LocalDateTime startAt, endAt;
    if (currTimes == 1) { // 尚未打卡
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt1()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt1()));
    } else if (currTimes == 2) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt2()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt2()));
    } else if (currTimes == 3) {
      startAt = (LocalDateTime.of(nowDate, activity.getPunchStartAt3()));
      endAt = (LocalDateTime.of(nowDate, activity.getPunchEndAt3()));
    } else {
      log.error("计算打卡周期数据异常: activity:{}, order:{}", activity, order);
      throw new RuntimeException("计算打卡周期数据异常");
    }
    // 活动结束时间
    if (punchAt.isAfter(activity.getEndDatetime())) {
      log.info("打卡时间{}处于活动结束时间{}", punchAt, activity.getEndDatetime());
      this.triggerCheckInFailure(order, activity, startAt, endAt);
      return true;
    }
    //
    if (punchAt.isAfter(endAt)) {
      //  实际打卡总次数 和 应打卡次数 是否一致？？？
      long days = activity.getPeriod().until(punchAt.toLocalDate(), ChronoUnit.DAYS) + 1;
      if (order.getTimes() != activity.getTimesOneday() * days) {
        log.info("打卡时间{}处于当前轮次结束时间{}", punchAt, endAt);
        this.triggerCheckInFailure(order, activity, startAt, endAt);
        return true;
      }
    }
    return false;
  }

  private boolean triggerCheckInFailure(NormalModeOrder order,
      NormalMode activity, LocalDateTime startAt, LocalDateTime endAt) {
    // 打卡失败, 更新订单为失败状态
    orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.fail.getCode());
    activityMapper.updatePunchFail(order.getActivityId(), order.getAmount());
    // 系统通知
    noticeService.addPunchResultNoticeForNormalMode(order.getUid(), activity.getPeriod(),
        activity.getTitle(), false, PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
    // 清空缓存
    redisUtils.del(this.getRKeyForActivity(activity.getId()));
    return true;
  }

  @Override public long triggerCheckInFailureByActivity(NormalMode activity) {
    long count;
    List<NormalModeOrder> orders = this.getOrdersByActivityId(activity.getId());
    log.info("触发打卡失败:: aid:{}/总{}个", activity.getId(), orders.size());
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
      // 获取参加此活动的所有用户订单
      List<NormalModeOrder> orders = this.getOrdersByActivityId(activity.getId());
      log.info("获取参加此[{}]活动的所有用户订单{}个", activity.getId(), orders.size());
      //
      long result = 0L;
      for (int i = 0; i < orders.size(); i++) {
        if (this.settlePrincipalForOrder(orders.get(i))) {
          result++;
        }
      }
      count = result;
      log.info("[{}] 结算本金的{}个", activity.getId(), count);
      // 此活动 已结算
      activityMapper.updateSettledComplete(activity.getId());
    } catch (Exception e) {
      log.error("结算常规本金 activity::{} ERR :: {}", activity, e);
    }
    return count;
  }

  private boolean settlePrincipalForOrder(NormalModeOrder order) {
    try {
      NormalMode activity = this.getActivityByIdFromRedis(order.getActivityId());
      // 触发下打卡是否失败 :: 如果存在，则获取新的order
      if (this.checkInPunchingTime(order)) {
        order = this.getOrderById(order.getId(), order.getUid());
      }
      // 订单成功方可执行以下操作
      if (PunchStatusCodes.success.getCode() == order.getStatus()) {
        // 订单是否成功，成功的话，本金支付到钱包中
        if (userMapper.updateWalletAdd(order.getUid(), order.getAmount()) == 1) {
          log.info("oid:{} uid:{} 本金已发放至钱包：{}", order.getId(), order.getUid(), order.getAmount());
          // 钱包增加记录
          String content = new StringBuilder().append("【")
              .append(activity.getTitle())
              .append("】")
              .append(WalletDtlTypeCodes.PUNCH_PRINCIPAL.getDesc()).toString().intern();

          WalletDtl walletDtl =
              new WalletDtl(order.getUid(), "+" + NumberUtil.format3Str(order.getAmount()),
                  content);
          walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes.PUNCH_PRINCIPAL);
        } else {
          log.info("### oid:{} uid:{} 本金更新钱包失败：{}", order.getId(), order.getUid(),
              order.getAmount());
        }
        return true;
      } else if (PunchStatusCodes.fail.getCode() == order.getStatus()) {
        log.info("打卡失败，扣除本金::{}", order);
      } else {
        log.debug("不应该存在的数据状态::?{}", order);
      }
    } catch (Exception e) {
      log.error("结算常规本金 order::{} ERR :: {}", order, e);
    }
    return false;
  }

  @Override public synchronized long settleBonusForActivity(NormalMode activity) {
    log.info("settleBonus 活动:: {}", activity);
    long count = 0;
    try {
      // 获取参加此活动的所有用户订单
      List<NormalModeOrder> orders = this.getNoFailOrdersByActivityId(activity.getId());
      log.info("settleBonus 获取参加此[{}]活动的所有用户订单{}个", activity.getId(), orders.size());
      // 奖池金额 =（失败金*80%）/ 周期天数
      BigDecimal bonusPool = activity.getFailTotalAmount()
          .multiply(new BigDecimal("0.80"))
          .divide(new BigDecimal(activity.getCycleDays()), 3, BigDecimal.ROUND_DOWN);
      // 成功人数
      int succPeople = activity.getTotalPeople() + activity.getDummyTotalPeople()
          - activity.getFailTotalPeople();
      log.info("settleBonus aid:{} 成功人数::{}", activity.getId(), succPeople);
      // 计算随机 红包算法
      List<BigDecimal> bonusList = Lists.newArrayList();
      RedPackageForWx.RedPackage moneyPackage = new RedPackageForWx.RedPackage();
      moneyPackage.remainMoney = bonusPool;
      moneyPackage.remainSize = succPeople < 0 ? 0 : succPeople;
      while (moneyPackage.remainSize != 0) {
        bonusList.add(RedPackageForWx.getRandomMoney(moneyPackage));
      }
      log.info("settleBonus aid:{} 红包算法::{}", activity.getId(), bonusList);
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
      log.info("[{}] 结算奖金的{}个", activity.getId(), count);
    } catch (Exception e) {
      log.error("ERR:: 结算常规奖金 activity::{} /{}", activity, e);
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
      // 活动是否支持每日结算奖金 :: 目前全部是日结的
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleBonusDaily())) {

      }
      //
      User user = userService.getByUidFromRedis(order.getUid());
      // 触发下打卡是否失败 :: 如果存在，则获取新的order
      if (this.checkInPunchingTime(order)) {
        order = this.getOrderById(order.getId(), order.getUid());
      }
      BigDecimal bonusPool, bonus, inviterBrokerage;
      // 奖金分配方式，A均分，B保底，R随机
      if (BonusMethodCodes.B.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! 保底
        // 是否按照费率还是固定金额
        bonus = DayMethodCodes.FA.getCode().equalsIgnoreCase(activity.getDayMethod())
            ? activity.getDailyFixedAmount()
            : (order.getAmount().multiply(activity.getDailyRate()));
      } else if (BonusMethodCodes.A.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! 均分
        // 平台扣除20% :: (总失败金*80%) / 周期天数
        activity = this.getActivityById(activity.getId());
        bonusPool = activity.getFailTotalAmount()
            .multiply(new BigDecimal("0.80"))
            .divide(new BigDecimal(activity.getCycleDays()), 3, BigDecimal.ROUND_DOWN);
        // 成功打卡人数
        int succPeople = activity.getTotalPeople() + activity.getDummyTotalPeople()
            - activity.getFailTotalPeople();
        // 人均奖金 = 可分配奖金 / 成功人数
        bonus = activity.getFailTotalPeople() == 0 ? new BigDecimal("0")
            : bonusPool.divide(new BigDecimal(succPeople), 6, BigDecimal.ROUND_DOWN);
      } else if (BonusMethodCodes.R.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        // !!! 随机
        activity = this.getActivityById(activity.getId());
        // 随机奖金: 可分配奖金 / 失败人数
        bonus = activity.getFailTotalPeople() == 0 ? new BigDecimal("0") : bonusForR;
      } else {
        log.error("未知的奖金分配方式::{}", activity);
        throw new Exception("未知的奖金分配方式:" + activity);
      }
      //
      inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
      // 存在邀请人，且不是保底分配
      if (StringUtils.isNotBlank(user.getInviterCode()) && user.getInviterUid() != null
          && !BonusMethodCodes.B.getCode().equalsIgnoreCase(activity.getBonusMethod())) {
        bonus = bonus.subtract(inviterBrokerage);
        log.info("uid:{}:: {}存在邀请人扣除佣金{}", order.getUid(), bonus, inviterBrokerage);
      }
      // 更新用户的钱包
      log.info("uid:{} 奖金：{}", order.getUid(), bonus);
      if (bonus.compareTo(BirdplanetConstants.ZERO_BD) == 1) {
        if (userMapper.updateWalletAdd(user.getUid(), bonus) == 1) {
          log.info("uid:{} 奖金已发放至钱包：{}", order.getUid(), bonus);
        } else {
          log.info("uid:{} 奖金更新钱包失败：{}", order.getUid(), bonus);
        }
      }
      // 钱包增加记录
      String content = new StringBuilder().append("【")
          .append(activity.getTitle())
          .append("】")
          .append(
              activity.getFailTotalPeople() != 0 ? WalletDtlTypeCodes.PUNCH_JL.getDesc() : "打卡奖励")
          .toString()
          .intern();
      WalletDtl walletDtl =
          new WalletDtl(order.getUid(), "+" + NumberUtil.format3Str(bonus),
              content);
      walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes.PUNCH_JL);

      // 是否存在邀请人
      if (StringUtils.isNotBlank(user.getInviterCode()) && user.getInviterUid() != null) {
        log.info("uid:{} 存在邀请人 uid:{}, code:{}", order.getUid(), user.getInviterUid(),
            user.getInvitationCode());
        // 邀请用户参加闯关赢取奖金部分的10%作为佣金奖励。
        log.info("uid:{} 邀请人的佣金：{}", order.getUid(), inviterBrokerage);
        //佣金++ , 佣金总额++
        userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(), inviterBrokerage);
        // 添加佣金记录
        brokerageService.addWithPunch(user.getInviterUid(), user.getUid(), user.getNickName(),
            activity.getTitle(), NumberUtil.format3Str(inviterBrokerage));
      }
      return true;
    } catch (Exception e) {
      log.error("结算常规本金 order::{} ERR :: {}", order, e);
      return false;
    }
  }

  private boolean addTimes(long oid) {
    return orderMapper.addTimesByOrderId(oid) == 1;
  }
}
