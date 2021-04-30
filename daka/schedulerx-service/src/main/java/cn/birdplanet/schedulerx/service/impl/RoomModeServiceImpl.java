package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.domain.po.RoomModeOrder;
import cn.birdplanet.daka.domain.po.RoomModeRound;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.schedulerx.persistence.punch.RoomModeMapper;
import cn.birdplanet.schedulerx.persistence.punch.RoomModeOrderMapper;
import cn.birdplanet.schedulerx.persistence.punch.RoomModeRoundMapper;
import cn.birdplanet.schedulerx.persistence.punch.UserMapper;
import cn.birdplanet.schedulerx.service.IBalanceService;
import cn.birdplanet.schedulerx.service.IBrokerageService;
import cn.birdplanet.schedulerx.service.INoticeService;
import cn.birdplanet.schedulerx.service.IRoomModeService;
import cn.birdplanet.schedulerx.service.IUserService;
import cn.birdplanet.schedulerx.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: v
 * @description: 房间模式的活动
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

  @Override public int updateStatusForActivityExpired(LocalDateTime endTime) {
    return activityMapper.updateStatusForActivityEnd(endTime);
  }

  @Override public int updateStatusForActivityStart(LocalDateTime startTime) {
    return activityMapper.updateStatusForActivityStart(startTime);
  }

  /**
   * 示例： room_mode:{period}:{template}:{id}
   *
   * @param aid 活动信息
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

  @Override public List<ActivityUserVo> getJoinedUsersByActivityId(long aid) {
    String rkey = this.getRKeyForJoinedUsers(aid);
    // 没有数据， 缓存可能清除，需要重新缓存
    if (redisUtils.hash_len(rkey) < 1) {
      List<ActivityUserVo> orders = orderMapper.getJoinedUsersByActivityId(aid);
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

  @Override public RoomModeOrder getOrderById(long oid, long uid) {
    RoomModeOrder order = orderMapper.selectByPrimaryKey(oid);
    return null != order ? (order.getUid() == uid ? order : null) : null;
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
    // 检查是否在签到时间内， 如果已经过了签到时间需要触发签到失败
    // 已有结果，不用再结算
    if (PunchStatusCodes.joining.getCode() != order.getStatus()) {
      return false;
    }
    // 当前的活动信息
    RoomMode activity = this.getActivityByIdFromRedis(order.getActivityId());
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

  private boolean triggerCheckInFailure(RoomModeOrder order,
      RoomMode activity, LocalDateTime startAt, LocalDateTime endAt) {
    // 打卡失败, 更新订单为失败状态
    orderMapper.updatePunchStatusById(order.getId(), PunchStatusCodes.fail.getCode());
    activityMapper.updatePunchFail(order.getActivityId(), order.getAmount());
    // 系统通知
    noticeService.addPunchResultNoticeForRoomMode(order.getUid(), activity.getPeriod(),
        activity.getTitle(), false, PunchUtils.buildCheckinTimeForNotice(startAt, endAt));
    // 清空缓存
    redisUtils.del(this.getRKeyForActivity(activity.getId()));
    return true;
  }

  @Override public long triggerCheckInFailureByActivity(RoomMode activity) {
    long count;
    List<RoomModeOrder> orders = this.getOrdersByActivityId(activity.getId());
    log.info("触发打卡失败:: aid:{}/总{}个", activity.getId(), orders.size());
    long result = 0L;
    for (RoomModeOrder order : orders) {
      log.info("aid:{} start >>>order:{}", order.getActivityId(), order);
      if (this.checkInPunchingTime(order)) {
        result++;
      }
      log.info("aid:{} end >>>oid:{}/result:{}", order.getActivityId(), order.getId(), result);
    }
    count = result;
    return count;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public long settleRoomActivity(final RoomMode activity) {
    log.info("当前房间 :: {}", activity);
    try {
      // 获取参加此活动的所有用户订单
      List<RoomModeOrder> orders = this.getOrdersByActivityId(activity.getId());
      log.info("此[{}]房间的所有人数{}个", activity.getId(), orders.size());
      // 获取签到成功订单
      List<RoomModeOrder> succOrders = orders.parallelStream()
          .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.success.getCode())
          .collect(Collectors.toList());
      log.info("打卡成功的人数:{}", succOrders.size());
      List<RoomModeOrder> failOrders = orders.parallelStream()
          .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.fail.getCode())
          .collect(Collectors.toList());
      BigDecimal failAmount = orders.parallelStream()
          .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.fail.getCode())
          .map(RoomModeOrder::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      log.info("打卡失败人数和金额:{}/{}", failOrders.size(), failAmount);

      // 失败的金额一致的时候进行处理
      if (failAmount.compareTo(activity.getFailTotalAmount()) == 0) {

        BigDecimal bonusPool = BirdplanetConstants.ZERO_BD;

        int totalIncomeRatio = activity.getIncomeRatio()
            + activity.getOwnerIncomeRatio()
            + activity.getPlatformIncomeRatio();
        if (totalIncomeRatio != 100) {
          throw new Exception("房间收益百分比有问题，不是100%，需重新分配");
        }
        // 用户分配的百分比
        BigDecimal incomeRatio =
            new BigDecimal(activity.getIncomeRatio()).divide(new BigDecimal(100), 3,
                BigDecimal.ROUND_DOWN);
        // 房主分配的百分比
        BigDecimal ownerIncomeRatio =
            new BigDecimal(activity.getOwnerIncomeRatio()).divide(new BigDecimal(100), 3,
                BigDecimal.ROUND_DOWN);
        // 如果有人失败(金额和人数都大于0)，分配失败金的80%，没有人失败： 扣除房主 挑战金的80%
        BigDecimal finalFailAmount = (activity.getFailTotalAmount().compareTo(BigDecimal.ZERO) == 1
            && activity.getFailTotalPeople() > 0)
            ? activity.getFailTotalAmount() : activity.getChallengeAmount();
        log.debug("finalFailAmount :: {}", finalFailAmount);
        // 房主的收益
        if (activity.getOwnerIncomeRatio().intValue() > 0) {
          log.debug("房主的收益 :: ");
          BigDecimal ownerAllocateAmount = finalFailAmount.multiply(ownerIncomeRatio);
          BigDecimal ownerPrincipalAndBonus =
              ownerAllocateAmount.add(activity.getChallengeAmount());
          // 订单是否成功，成功的话，支付到钱包中
          if (userMapper.updateWalletAdd(activity.getOwnerUid(), ownerPrincipalAndBonus) == 1) {
            log.info("oid:{} uid:{} 本金和奖金已发放至钱包：{}", activity.getId(), activity.getOwnerUid(),
                ownerPrincipalAndBonus);
            // 钱包增加记录
            String content = new StringBuilder().append("【")
                .append(activity.getTitle())
                .append("】")
                .append(WalletDtlTypeCodes._PUNCH_RM_OWNER.getDesc()).toString().intern();

            WalletDtl walletDtl =
                new WalletDtl(activity.getOwnerUid(),
                    "+" + NumberUtil.format3Str(ownerPrincipalAndBonus),
                    content);
            walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._PUNCH_RM_OWNER);
            //
            bonusPool = bonusPool.add(ownerPrincipalAndBonus);
          }
        }
        // 此房间人数全部是失败， 不进行分配处理
        if (activity.getFailTotalPeople().intValue() == activity.getTotalPeople() &&
            activity.getFailTotalPeople() != 0) {
          log.info("此房间人数全部失败，不进行分配处理");
          activityMapper.updateBonusPoolById(activity.getId(), BirdplanetConstants.ZERO_BD);
          return 0L;
        }
        // 打卡人的可分配金额
        BigDecimal allocateAmount = finalFailAmount.multiply(incomeRatio);
        // 平均每个人的打卡人的可分配金额 : 可分配金额/（成功的人数 + 房间的假人数）/ 最大倍数
        BigDecimal avgBonus = allocateAmount.divide(
            new BigDecimal(succOrders.size() + activity.getDummyTotalPeople()), 3,
            BigDecimal.ROUND_DOWN)
            .divide(new BigDecimal(this.getMaxMultiple(activity.getMultiple())), 3,
                BigDecimal.ROUND_DOWN);
        // 每个的金额可能不一样： 如果加倍的话
        BigDecimal onePrincipalAndBonus;
        WalletDtl walletDtl;
        // 有人失败，分配失败的80%， 没人失败
        for (RoomModeOrder order : succOrders) {
          // 本金 + （平均的分配金额* 倍数）
          onePrincipalAndBonus =
              avgBonus.multiply(new BigDecimal(order.getMultiple())).add(order.getAmount());
          //
          if (PunchStatusCodes.success.getCode() == order.getStatus()) {

            // 订单是否成功，成功的话，支付到钱包中
            if (userMapper.updateWalletAdd(order.getUid(), onePrincipalAndBonus) == 1) {
              log.info("oid:{} uid:{} 本金和奖金已发放至钱包：{}", order.getId(), order.getUid(),
                  onePrincipalAndBonus);
              // 钱包增加记录
              String content = new StringBuilder().append("【")
                  .append(activity.getTitle())
                  .append("】")
                  .append(WalletDtlTypeCodes._PUNCH_RM.getDesc()).toString().intern();

              walletDtl =
                  new WalletDtl(order.getUid(), "+" + NumberUtil.format3Str(onePrincipalAndBonus),
                      content);
              walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._PUNCH_RM);
              //
              bonusPool = bonusPool.add(onePrincipalAndBonus);
            } else {
              log.info("### oid:{} uid:{} 本金更新钱包失败：{}", order.getId(), order.getUid(),
                  onePrincipalAndBonus);
            }
          }
        }
        // 更新已经分配的金额, 活动结束
        activityMapper.updateBonusPoolById(activity.getId(), bonusPool);
        return succOrders.size();
      } else {
        log.error("结算房间 activity::{} 失败金额不一致。需手动处理", activity);
      }
    } catch (Exception e) {
      log.error("结算房间 activity::{} ERR :: {}", activity, e);
    }
    return -1;
  }

  /**
   * 获取最大的倍数
   *
   * @param multiples， "1，2，3，4"
   * @return
   */
  private int getMaxMultiple(String multiples) {
    try {
      // 分割后的 字符必须是数字， 方可进行排序
      return Arrays.stream(multiples.split(","))
          .filter(s -> StringUtils.isNumeric(s))
          .map(s -> Integer.parseInt(s.trim()))
          .collect(Collectors.toList())
          .stream()
          .max(Integer::compareTo)
          .orElse(1);
    } catch (Exception e) {
      log.info("获取最大的倍数 异常， 按1倍处理");
      return 1;
    }
  }
}
