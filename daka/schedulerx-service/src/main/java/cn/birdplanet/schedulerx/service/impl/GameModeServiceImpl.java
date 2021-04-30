/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.GameModeBenefit;
import cn.birdplanet.daka.domain.po.GameModeBonusMax;
import cn.birdplanet.daka.domain.po.GameModeBonusTiered;
import cn.birdplanet.daka.domain.po.GameModeGear;
import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.domain.po.GameModeSettleRecord;
import cn.birdplanet.daka.domain.po.GameModeTemplate;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthVO;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthWithDayVO;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.schedulerx.persistence.punch.GameModeBenefitMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeBonusMaxMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeBonusTieredMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeOrderMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeRoundMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeSettleRecordMapper;
import cn.birdplanet.schedulerx.persistence.punch.GameModeTemplateMapper;
import cn.birdplanet.schedulerx.persistence.punch.Grid9Mapper;
import cn.birdplanet.schedulerx.persistence.punch.RoundMapper;
import cn.birdplanet.schedulerx.persistence.punch.UserMapper;
import cn.birdplanet.schedulerx.service.IBalanceService;
import cn.birdplanet.schedulerx.service.IBrokerageService;
import cn.birdplanet.schedulerx.service.IGameModeService;
import cn.birdplanet.schedulerx.service.INoticeService;
import cn.birdplanet.schedulerx.service.IUserService;
import cn.birdplanet.schedulerx.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityStatusCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import cn.birdplanet.toolkit.extra.code.SettleTypeCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
 */
@Slf4j
@Service
public class GameModeServiceImpl extends BaseService implements IGameModeService {

  @Autowired private IUserService userService;
  @Autowired private INoticeService noticeService;
  @Autowired private IBalanceService balanceService;
  @Autowired private IBrokerageService brokerageService;
  @Autowired private IWalletService walletService;

  @Autowired private UserMapper userMapper;
  @Autowired private GameModeBonusMaxMapper gameModeBonusMaxMapper;
  @Autowired private GameModeBonusTieredMapper gameModeBonusTieredMapper;
  @Autowired private RoundMapper roundMapper;
  @Autowired private Grid9Mapper grid9Mapper;
  @Autowired private GameModeMapper gameModeMapper;
  @Autowired private GameModeTemplateMapper gameModeTemplateMapper;
  @Autowired private GameModeOrderMapper gameModeOrderMapper;
  @Autowired private GameModeRoundMapper gameModeRoundMapper;
  @Autowired private GameModeBenefitMapper gameModeBenefitMapper;
  @Autowired private GameModeSettleRecordMapper gameModeSettleRecordMapper;

  @Override public List<GameModeOrder> getPunchOrdersByActivityId(long activityId) {
    Example example = new Example(GameModeOrder.class);
    example.createCriteria().andEqualTo("activityId", activityId);
    example.orderBy("id").desc();
    return gameModeOrderMapper.selectByExample(example);
  }

  @Override public List<GameModeGrid9> getGrid9Data(GameModeActivityTypeCodes codes) {
    if (null == codes) { // 使用最低额收益金额
      codes = GameModeActivityTypeCodes.A;
    }
    String keys = RedisConstants.GAME_MODE_GRID9_KEY_PREFIX + codes.getCode();
    List<GameModeGrid9> grid9List = (List<GameModeGrid9>) redisUtils.get(keys);
    if (null == grid9List || grid9List.isEmpty()) {
      Example example = new Example(GameModeGrid9.class);
      example.createCriteria().andEqualTo("status", "Y")
          .andEqualTo("type", codes.getCode());
      example.orderBy("seq").desc().orderBy("amount").desc();
      grid9List = grid9Mapper.selectByExample(example);
      List<GameModeGrid9> finalGrid9List = grid9List;
      taskExecutor.execute(() -> {
        // 加入 redis
        redisUtils.set1Week(keys, finalGrid9List);
      });
    }
    return grid9List;
  }

  private GameModeGrid9 getMaxMultipleGrid9Data(GameModeActivityTypeCodes codes) {
    List<GameModeGrid9> grid9List = this.getGrid9Data(codes);
    GameModeGrid9 maxMultipleData =
        grid9List.stream().max(Comparator.comparingLong(GameModeGrid9::getId)).get();
    return maxMultipleData;
  }

  /**
   * 组合活动编码
   *
   * @param activity 活动信息
   * @return 组合活动编码
   */
  private String getOrderSn(final GameMode activity) {
    return new StringBuilder(20).append("GM")
        .append(activity.getId())
        .append("-")
        .append(activity.getMaxRound())
        .append(activity.getType())
        .toString();
  }

  @Override
  public GameModeRound getPunchRoundByPunchOrder(long uid, long punchOrderId, int currentRound) {
    Example example = new Example(GameModeRound.class);
    example.createCriteria()
        .andEqualTo("uid", uid)
        .andEqualTo("punchId", punchOrderId)
        .andEqualTo("round", currentRound);
    return gameModeRoundMapper.selectOneByExample(example);
  }

  /**
   * 获取第几轮次 需要的打卡时间分钟
   */
  private GameModeGear getGearByRound(int round, String type) {

    String key = RedisConstants.GAME_MODE_GEAR_KEY_PREFIX + type + ":" + round;
    GameModeGear punchGear = (GameModeGear) redisUtils.get(key);
    if (null == punchGear) {
      Example example = new Example(GameModeGear.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("round", round).andEqualTo("type", type);
      punchGear = roundMapper.selectOneByExample(example);
      if (null != punchGear) {
        redisUtils.set1Month(key, punchGear);
      }
    }
    return punchGear;
  }

  @Override public LocalDate getCurrPeriod() {
    LocalDateTime currLdt = LocalDateTime.now();
    // 如果时间是八点之前  获取 前一天的
    if (currLdt.getHour() < 8) {
      currLdt = currLdt.minusDays(1);
    }
    return currLdt.toLocalDate();
  }

  private String getCurrPeriodForRedisKey(LocalDate localDate) {
    return RedisConstants.GAME_MODE_KEY_PREFIX + PunchUtils.punchPeriod(localDate);
  }

  private List<GameModeBonusMax> getAllGameModeBonus(GameModeActivityTypeCodes codes) {
    String keys = RedisConstants.GAME_MODE_BONUS_LIST_KEY_PREFIX + codes.getCode();
    List<GameModeBonusMax> bonusList = (List<GameModeBonusMax>) redisUtils.get(keys);
    if (null == bonusList || bonusList.isEmpty()) {
      Example example = new Example(GameModeBonusMax.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("type", codes.getCode()).andEqualTo("status", YesOrNoCodes.YES.getCode());
      bonusList = gameModeBonusMaxMapper.selectByExample(example);
      if (!bonusList.isEmpty()) {
        // 存入redis
        List<GameModeBonusMax> finalBonusList = bonusList;
        taskExecutor.execute(() -> redisUtils.set1Month(keys, finalBonusList));
      }
    }
    return bonusList;
  }

  private BigDecimal getGameModeBonusMaxRandom(GameModeActivityTypeCodes codes) {
    List<GameModeBonusMax> bonusList = this.getAllGameModeBonus(codes);
    // 随机排序
    Collections.shuffle(bonusList);
    BigDecimal bonus;
    try {
      bonus = bonusList.get(0).getBonus();
    } catch (Exception e) {
      log.error("ERR 随机的奖金", e);
      bonus = new BigDecimal("13.50");
    }
    log.info("随机的奖金:{}", bonus);
    return bonus;
  }

  @Override public List<GameMode> getActivitiesByPeriod(LocalDate period) {
    Example example = new Example(GameMode.class);
    // 使用字符串进行日期的比对
    example.createCriteria().andCondition("period = '" + period + "'");
    List<GameMode> list = gameModeMapper.selectByExample(example);
    return list;
  }

  @Override public GameMode getActivityById(long aid) {
    String key = RedisConstants.GAME_MODE_ID_KEY_PREFIX + aid;
    GameMode activity = (GameMode) redisUtils.get(key);
    if (null == activity) {
      activity = gameModeMapper.selectByPrimaryKey(aid);
      if (null != activity) {
        redisUtils.set2Day(key, activity);
      }
    }
    return activity;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean settle(final GameMode activity) {
    log.info("结算活动:{}", activity);
    // 如果结算活动是阶梯方式的话：
    if (activity.getSettleType().equalsIgnoreCase(SettleTypeCodes.JT.getCode())) {
      return this.settleWithJT(activity);
    }
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加的人数:{}", orders.size());
    // 获取签到成功订单
    List<GameModeOrder> succOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.success.getCode())
        .collect(Collectors.toList());
    // 分配钱的人数
    int succTotalPeople = succOrders.size() + activity.getDummyTotalPeople();
    log.info("打卡成功的人数:{} + {}", succOrders.size(), activity.getDummyTotalPeople());
    // 失败金 = (总人数-失败人数)* 奖励基数
    BigDecimal _300_9 =
        this.getGameModeBonusMaxRandom(GameModeActivityTypeCodes.codeOf(activity.getType()));
    int succPeople = activity.getTotalPeople() - activity.getFailTotalPeople();
    BigDecimal bonusPool = _300_9.multiply(new BigDecimal(succPeople)),
        bonusPoolReal = BirdplanetConstants.ZERO_BD;
    log.info("失败金:{}", bonusPool);

    BigDecimal maxMultiple =
        this.getMaxMultipleGrid9Data(GameModeActivityTypeCodes.codeOf(activity.getType()))
            .getAmount();
    log.info(".getGameModeBonusMaxRandom:{}", maxMultiple);
    // 最小粒度的奖金 : 成功人数*支付的金额/最大倍数/最大轮数）, 小数点之后 直接舍弃
    BigDecimal miniBonus = (succTotalPeople == 0) ? new BigDecimal("0.00") :
        bonusPool.divide(
            (maxMultiple.multiply(new BigDecimal(activity.getMaxRound() * succTotalPeople))),
            6, BigDecimal.ROUND_DOWN);
    log.info("{}期{}/人均最小额度的奖金:{}", activity.getId(), PunchUtils.punchPeriod(activity.getPeriod()),
        miniBonus);

    // 分钱
    for (GameModeOrder order : orders) {
      GameModeSettleRecord settleRecord = new GameModeSettleRecord(order);
      // 防止数据异常的
      if (order.getStatus() == PunchStatusCodes.success.getCode()) {
        // 当前的用户信息
        User user = userService.getByUid(order.getUid());
        // 奖金 = 最小奖金*参加的轮次{大于最大轮数则按最大轮处理}*倍数
        BigDecimal bonus = order.getAmount()
            .multiply(new BigDecimal(
                order.getJoinedRounds() <= activity.getMaxRound() ? order.getJoinedRounds()
                    : activity.getMaxRound()))
            .multiply(miniBonus);
        log.info("uid:{} {}轮/奖金：{}", order.getUid(), order.getJoinedRounds(), bonus);
        // 计算实际的奖金发放
        bonusPoolReal = bonusPoolReal.add(bonus);
        // 是否存在邀请人
        if (StringUtils.isNotBlank(user.getInviterCode())) {
          // 不为空，且佣金关系为Y的时候 结算佣金
          if (StringUtils.isNotBlank(activity.getIsSettleCommission()) &&
              YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleCommission())) {

            log.info("uid:{} 存在邀请人 uid:{}, code:{}", order.getUid(), user.getInviterUid(),
                user.getInvitationCode());
            // 邀请用户参加闯关赢取奖金部分的10%作为佣金奖励。
            BigDecimal inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
            log.info("uid:{} 邀请人的佣金：{}", order.getUid(), inviterBrokerage);
            // 佣金++ , 佣金总额++
            userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(),
                inviterBrokerage);
            //
            settleRecord.setInviterBrokerage(inviterBrokerage);
            // 添加佣金记录
            brokerageService.addWithPunch(user.getInviterUid(), user.getUid(),
                user.getNickName(), order.getPeriod(), NumberUtil.format3Str(inviterBrokerage));

            // 扣除邀请人的佣金部分
            bonus = bonus.subtract(inviterBrokerage);
            log.info("uid:{} 扣除佣金后的奖金：{}", order.getUid(), bonus);
          }
        }
        // 更新用户的钱包
        BigDecimal principalAndBonus = order.getAmount().add(bonus);
        log.info("uid:{} 本金+奖金：{}", order.getUid(), principalAndBonus);
        if (userMapper.updateWalletAdd(user.getUid(), principalAndBonus) == 1) {
          log.info("uid:{} 已发放至钱包：{}", order.getUid(), principalAndBonus);
          // 更新用户收益明细
          userMapper.updateIncomeSumAdd(user.getUid(), bonus);
          log.info("uid:{} 收益总额：{}", order.getUid(), principalAndBonus);
        } else {
          log.info("uid:{} 更新钱包失败：{}", order.getUid(), principalAndBonus);
        }
        // 钱包增加记录
        WalletDtl walletDtl =
            new WalletDtl(user.getUid(), "+" + NumberUtil.format3Str(principalAndBonus),
                new StringBuffer().append(PunchUtils.punchPeriod(order.getPeriod()))
                    .append("期「")
                    .append(activity.getTitle())
                    .append("」")
                    .append(WalletDtlTypeCodes._punch.getDesc())
                    .toString());
        walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._punch);
        //
        settleRecord.setBonus(bonus);
      } else {
        //
        settleRecord.setBonus(BirdplanetConstants.ZERO_BD);
        settleRecord.setInviterBrokerage(BirdplanetConstants.ZERO_BD);
      }
      // 发放额外奖励
      List<GameModeBenefit> benefits =
          this.getAllAvailableBenefits(GameModeActivityTypeCodes.codeOf(activity.getType()));
      BigDecimal benefitCount = BirdplanetConstants.ZERO_BD;
      if (!benefits.isEmpty()) {
        for (GameModeBenefit benefit : benefits) {
          // 金额一直 且 打卡的轮数要大于等于规定的轮数
          if (benefit.getPunchAmount().compareTo(order.getAmount()) == 0
              && order.getJoinedRounds().intValue() >= benefit.getPunchRound().intValue()) {
            // 需要打卡成功，进行奖励
            if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(benefit.getNeedPunchSucc())) {
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            } else { // 不需要成功打卡，进行奖励
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            }
          }
        }
      }
      //
      settleRecord.setBenefits(benefitCount);
      // 结算记录数据库
      gameModeSettleRecordMapper.insertSelective(settleRecord);
      // 计算真实的分配奖金
      bonusPoolReal = bonusPoolReal.add(benefitCount);
    }
    // 结算后更改状态
    return gameModeMapper.updateActivitySettleComplete(activity.getId(), bonusPool, bonusPoolReal)
        == 1;
  }

  /**
   * todo 阶梯收益方式
   *
   * @param activity
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean settleWithJT(final GameMode activity) {
    log.info("结算活动:{}", activity);
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加的人数:{}", orders.size());
    // 获取签到成功订单
    List<GameModeOrder> succOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.success.getCode())
        .collect(Collectors.toList());
    log.info("打卡成功的人数:{}", succOrders.size());
    BigDecimal bonusPoolReal = BirdplanetConstants.ZERO_BD;

    // 获取该活动类型：阶梯收益的数据
    List<GameModeBonusTiered> tieredBonusList =
        this.getAllGameModeBonusTiered(GameModeActivityTypeCodes.codeOf(activity.getType()));
    // 已经计算好的阶梯奖金
    Map<Integer, BigDecimal> tieredBonusMap = this.settleTieredBonus(tieredBonusList);

    // 分钱
    for (GameModeOrder order : orders) {
      GameModeSettleRecord settleRecord = new GameModeSettleRecord(order);
      // 防止数据异常的
      if (order.getStatus() == PunchStatusCodes.success.getCode()) {
        // 当前的用户信息
        User user = userService.getByUid(order.getUid());
        // 奖金 =
        BigDecimal bonus = tieredBonusMap.get(order.getJoinedRounds());
        log.info("uid:{} {}轮/奖金：{}", order.getUid(), order.getJoinedRounds(), bonus);
        // 计算实际的奖金发放
        bonusPoolReal = bonusPoolReal.add(bonus);
        // 是否存在邀请人
        if (StringUtils.isNotBlank(user.getInviterCode())) {
          // 不为空，且佣金关系为Y的时候 结算佣金
          if (StringUtils.isNotBlank(activity.getIsSettleCommission()) &&
              YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleCommission())) {

            log.info("uid:{} 存在邀请人 uid:{}, code:{}", order.getUid(), user.getInviterUid(),
                user.getInvitationCode());
            // 邀请用户参加闯关赢取奖金部分的10%作为佣金奖励。
            BigDecimal inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
            log.info("uid:{} 邀请人的佣金：{}", order.getUid(), inviterBrokerage);
            // 佣金++ , 佣金总额++
            userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(),
                inviterBrokerage);
            //
            settleRecord.setInviterBrokerage(inviterBrokerage);
            // 添加佣金记录
            brokerageService.addWithPunch(user.getInviterUid(), user.getUid(),
                user.getNickName(), order.getPeriod(), NumberUtil.format3Str(inviterBrokerage));

            // 扣除邀请人的佣金部分
            bonus = bonus.subtract(inviterBrokerage);
            log.info("uid:{} 扣除佣金后的奖金：{}", order.getUid(), bonus);
          }
        }
        // 更新用户的钱包
        BigDecimal principalAndBonus = order.getAmount().add(bonus);
        log.info("uid:{} 本金+奖金：{}", order.getUid(), principalAndBonus);
        if (userMapper.updateWalletAdd(user.getUid(), principalAndBonus) == 1) {
          log.info("uid:{} 已发放至钱包：{}", order.getUid(), principalAndBonus);
          // 更新用户收益明细
          userMapper.updateIncomeSumAdd(user.getUid(), bonus);
          log.info("uid:{} 收益总额：{}", order.getUid(), principalAndBonus);
        } else {
          log.info("uid:{} 更新钱包失败：{}", order.getUid(), principalAndBonus);
        }
        // 钱包增加记录
        WalletDtl walletDtl =
            new WalletDtl(user.getUid(), "+" + NumberUtil.format3Str(principalAndBonus),
                new StringBuffer().append(PunchUtils.punchPeriod(order.getPeriod()))
                    .append("期「")
                    .append(activity.getTitle())
                    .append("」")
                    .append(WalletDtlTypeCodes._punch.getDesc())
                    .toString());
        walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._punch);
        //
        settleRecord.setBonus(bonus);
      } else {
        //
        settleRecord.setBonus(BirdplanetConstants.ZERO_BD);
        settleRecord.setInviterBrokerage(BirdplanetConstants.ZERO_BD);
      }
      // 发放额外奖励
      List<GameModeBenefit> benefits =
          this.getAllAvailableBenefits(GameModeActivityTypeCodes.codeOf(activity.getType()));
      BigDecimal benefitCount = BirdplanetConstants.ZERO_BD;
      if (!benefits.isEmpty()) {
        for (GameModeBenefit benefit : benefits) {
          // 金额一直 且 打卡的轮数要大于等于规定的轮数
          if (benefit.getPunchAmount().compareTo(order.getAmount()) == 0
              && order.getJoinedRounds().intValue() >= benefit.getPunchRound().intValue()) {
            // 需要打卡成功，进行奖励
            if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(benefit.getNeedPunchSucc())) {
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            } else { // 不需要成功打卡，进行奖励
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            }
          }
        }
      }
      //
      settleRecord.setBenefits(benefitCount);
      // 结算记录数据库
      gameModeSettleRecordMapper.insertSelective(settleRecord);
      // 计算真实的分配奖金
      bonusPoolReal = bonusPoolReal.add(benefitCount);
    }
    // 结算后更改状态
    return gameModeMapper.updateActivitySettleComplete(activity.getId(), bonusPoolReal,
        bonusPoolReal)
        == 1;
  }

  /**
   * todo 成功： 保底分配，失败：分配失败的80%
   *
   * @param activity
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean settleWithBD(final GameMode activity) {
    log.info("保底分配 》》》结算活动:{}", activity);
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加的人数:{}", orders.size());
    // 获取签到成功订单
    List<GameModeOrder> succOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.success.getCode())
        .collect(Collectors.toList());
    // 分配钱的人数
    int succTotalPeople = succOrders.size() + activity.getDummyTotalPeople();
    log.info("打卡成功的人数:{} + {}", succOrders.size(), activity.getDummyTotalPeople());
    // 如果人数总计不一致， 则不处理
    if (activity.getFailTotalPeople().intValue() != (activity.getTotalPeople().intValue()
        - succOrders.size())) {
      log.error("人数和实际的人数不一致！！！ {}+{}={}", succOrders.size(), activity.getFailTotalPeople(),
          activity.getTotalPeople());
      return false;
    }
    // 保底模式下： 分配比例 不能大于1
    if (activity.getSettleType().startsWith("BD")
        && activity.getSuccIncomeRatio().compareTo(BirdplanetConstants.BD1) == 1) {
      log.error("保底模式下：{} 分配比例 不能大于1", activity.getSuccIncomeRatio());
      return false;
    }
    //
    BigDecimal bonusPool = BirdplanetConstants.ZERO_BD;
    BigDecimal bonusPoolReal = BirdplanetConstants.ZERO_BD;
    // 成功： 保底分配，失败：分配总失败金额的80%
    BigDecimal avgBonus = BirdplanetConstants.ZERO_BD;
    if (activity.getSettleType().equalsIgnoreCase(SettleTypeCodes.BD0.getCode())) {
      log.info("不保底结算模式 ::{}", SettleTypeCodes.BD0);
      //
      try {
        // 失败金额大于0在进行分配
        if (activity.getFailTotalAmount().compareTo(BirdplanetConstants.ZERO_BD) == 1) {
          bonusPool = activity.getFailTotalAmount().multiply(activity.getSuccIncomeRatio());
          avgBonus = bonusPool.divide(new BigDecimal(succTotalPeople), 3, BigDecimal.ROUND_DOWN);
        }
      } catch (Exception e) {
        log.error("失败总金额*（80/100）/成功的人数::", e);
      }
    } else {
      log.info("保底结算模式 ::{}", activity.getSettleType());
      // 全部成功： 保底分配
      if (activity.getFailTotalPeople() == 0 && succOrders.size() == activity.getTotalPeople()) {
        // 保底金额 ::
        avgBonus = activity.getBdBonusPool();
        log.info("全部成功 进行保底计算 ::{}", avgBonus);
      } else {
        //
        try {
          bonusPool = activity.getFailTotalAmount().multiply(activity.getSuccIncomeRatio());
          avgBonus = bonusPool.divide(new BigDecimal(succTotalPeople), 3, BigDecimal.ROUND_DOWN);
          log.info("存在失败人数 进行{}失败金分配计算 ::{}", activity.getSuccIncomeRatio(), avgBonus);
        } catch (Exception e) {
          log.error("失败总金额*（80/100）/成功的人数::", e);
        }
      }
    }
    log.info("{}轮{}保底金额 ::{}", activity.getMaxRound(), activity.getBdBonusPool(), avgBonus);
    // 每一轮的保底金额 ！！！
    avgBonus = avgBonus.divide(new BigDecimal(activity.getMaxRound()), 3, BigDecimal.ROUND_DOWN);
    log.info("1轮的保底金额 ::{}", avgBonus);

    // 分钱
    for (GameModeOrder order : orders) {
      GameModeSettleRecord settleRecord = new GameModeSettleRecord(order);
      // 防止数据异常的
      if (order.getStatus() == PunchStatusCodes.success.getCode()) {
        // 当前的用户信息
        User user = userService.getByUid(order.getUid());
        // 奖金: 每一轮的奖金* 参加的轮数
        BigDecimal bonus = avgBonus.multiply(new BigDecimal(order.getJoinedRounds()));
        log.info("uid:{} {}轮/奖金：{}", order.getUid(), order.getJoinedRounds(), bonus);
        // 计算实际的奖金发放
        bonusPoolReal = bonusPoolReal.add(bonus);
        // 是否存在邀请人
        if (StringUtils.isNotBlank(user.getInviterCode())) {
          // 不为空，且佣金关系为Y的时候 结算佣金
          if (StringUtils.isNotBlank(activity.getIsSettleCommission()) &&
              YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleCommission())) {

            log.info("uid:{} 存在邀请人 uid:{}, code:{}", order.getUid(), user.getInviterUid(),
                user.getInvitationCode());
            // 邀请用户参加闯关赢取奖金部分的10%作为佣金奖励。
            BigDecimal inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
            log.info("uid:{} 邀请人的佣金：{}", order.getUid(), inviterBrokerage);
            // 佣金++ , 佣金总额++
            userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(),
                inviterBrokerage);
            //
            settleRecord.setInviterBrokerage(inviterBrokerage);
            // 添加佣金记录
            brokerageService.addWithPunch(user.getInviterUid(), user.getUid(),
                user.getNickName(), order.getPeriod(), NumberUtil.format3Str(inviterBrokerage));

            // 扣除邀请人的佣金部分
            bonus = bonus.subtract(inviterBrokerage);
            log.info("uid:{} 扣除佣金后的奖金：{}", order.getUid(), bonus);
          }
        }
        // 更新用户的钱包
        BigDecimal principalAndBonus = order.getAmount().add(bonus);
        log.info("uid:{} 本金+奖金：{}", order.getUid(), principalAndBonus);
        if (userMapper.updateWalletAdd(user.getUid(), principalAndBonus) == 1) {
          log.info("uid:{} 已发放至钱包：{}", order.getUid(), principalAndBonus);
          // 更新用户收益明细
          userMapper.updateIncomeSumAdd(user.getUid(), bonus);
          log.info("uid:{} 收益总额：{}", order.getUid(), principalAndBonus);
        } else {
          log.info("uid:{} 更新钱包失败：{}", order.getUid(), principalAndBonus);
        }
        // 钱包增加记录
        WalletDtl walletDtl =
            new WalletDtl(user.getUid(), "+" + NumberUtil.format3Str(principalAndBonus),
                new StringBuffer().append(PunchUtils.punchPeriod(order.getPeriod()))
                    .append("期「")
                    .append(activity.getTitle())
                    .append("」")
                    .append(WalletDtlTypeCodes._punch.getDesc())
                    .toString());
        walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._punch);
        //
        settleRecord.setBonus(bonus);
      } else {
        //
        settleRecord.setBonus(BirdplanetConstants.ZERO_BD);
        settleRecord.setInviterBrokerage(BirdplanetConstants.ZERO_BD);
      }
      // 发放额外奖励
      List<GameModeBenefit> benefits =
          this.getAllAvailableBenefits(GameModeActivityTypeCodes.codeOf(activity.getType()));
      BigDecimal benefitCount = BirdplanetConstants.ZERO_BD;
      if (!benefits.isEmpty()) {
        for (GameModeBenefit benefit : benefits) {
          // 金额一直 且 打卡的轮数要大于等于规定的轮数
          if (benefit.getPunchAmount().compareTo(order.getAmount()) == 0
              && order.getJoinedRounds().intValue() >= benefit.getPunchRound().intValue()) {
            // 需要打卡成功，进行奖励
            if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(benefit.getNeedPunchSucc())) {
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            } else { // 不需要成功打卡，进行奖励
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            }
          }
        }
      }
      //
      settleRecord.setBenefits(benefitCount);
      // 结算记录数据库
      gameModeSettleRecordMapper.insertSelective(settleRecord);
      // 计算真实的分配奖金
      bonusPoolReal = bonusPoolReal.add(benefitCount);
    }
    // 结算后更改状态
    return gameModeMapper.updateActivitySettleComplete(activity.getId(), bonusPool, bonusPoolReal)
        == 1;
  }

  private synchronized Map<Integer, BigDecimal> settleTieredBonus(
      List<GameModeBonusTiered> tieredBonusList) {
    Map<Integer, BigDecimal> dataMap = Maps.newHashMapWithExpectedSize(tieredBonusList.size());
    for (GameModeBonusTiered bonusTiered : tieredBonusList) {
      dataMap.put(bonusTiered.getRound(), tieredBonusList.stream()
          .filter(gameModeBonusTiered -> gameModeBonusTiered.getRound() <= bonusTiered.getRound())
          .map(GameModeBonusTiered::getBonus)
          .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    return dataMap;
  }

  private List<GameModeBonusTiered> getAllGameModeBonusTiered(GameModeActivityTypeCodes codes) {
    String keys = RedisConstants.GAME_MODE_BONUS_LIST_TIERED_KEY_PREFIX + codes.getCode();
    List<GameModeBonusTiered> bonusList = (List<GameModeBonusTiered>) redisUtils.get(keys);
    if (null == bonusList || bonusList.isEmpty()) {
      Example example = new Example(GameModeBonusTiered.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("type", codes.getCode()).andEqualTo("status", YesOrNoCodes.YES.getCode());
      bonusList = gameModeBonusTieredMapper.selectByExample(example);
      if (!bonusList.isEmpty()) {
        // 存入redis
        List<GameModeBonusTiered> finalBonusList = bonusList;
        taskExecutor.execute(() -> redisUtils.set1Month(keys, finalBonusList));
      }
    }
    return bonusList;
  }

  @Override public boolean updateActivityComplete(long aid) {
    return gameModeMapper.updateActivityStatus(aid, GameModeActivityStatusCodes.finish.getCode())
        == 1;
  }

  @Override public boolean updateActivitySettle(long aid) {
    return gameModeMapper.updateActivityStatus(aid, GameModeActivityStatusCodes.settle.getCode())
        == 1;
  }

  @Override public List<PunchSumVO> getPunchSumByMonth(LocalDate firstDay,
      LocalDate lastDay, int joinedRoundsSum, final BigDecimal amountLevel) {
    return gameModeOrderMapper.getPunchSumByMonth_gt(firstDay, lastDay, joinedRoundsSum,
        amountLevel);
  }

  @Override public List<PunchSumVO> getPunchSumByMonthForFreeze(LocalDate firstDay,
      LocalDate lastDay, int joinedRoundsSum, final BigDecimal amountLevel) {
    return gameModeOrderMapper.getPunchSumByMonth_lt(firstDay, lastDay, joinedRoundsSum,
        amountLevel);
  }

  @Override public synchronized boolean generateNextActivityWithTemplate(LocalDate period) {
    Example example = new Example(GameModeTemplate.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", "Y");
    List<GameModeTemplate> templates = gameModeTemplateMapper.selectByExample(example);
    if (templates.isEmpty()) {
      log.info("没有查询到闯关的模版任务");
      return true;
    }
    // 根据模版生成新的闯关 ?? todo 自动装配 autoAssembly
    templates.forEach(gameModeTemplate -> {
      GameMode activity = new GameMode(gameModeTemplate, period);
      boolean flag = gameModeMapper.insertSelective(activity) == 1;
      if (flag) {
        // 更新 模版的last_period
        gameModeTemplateMapper.updateLastPeriodById(gameModeTemplate.getId(), period);
      }
    });
    return false;
  }

  @Override public void statisticsByMonthWithDay(LocalDate firstDay, LocalDate endDay) {
    // todo 只统计当前月闯关的人数，
    List<Long> uids = gameModeOrderMapper.getUidsWithJoinedInCurrDate(firstDay, endDay);
    if (!uids.isEmpty()) {
      List<PunchJoinRoundSumByMonthVO> monthVOS;
      List<PunchJoinRoundSumByMonthWithDayVO> dayVOS;
      Map<String, Object> dataMap = new HashMap();
      for (long uid : uids) {
        monthVOS = gameModeOrderMapper.statisticsByMonth(uid, firstDay, endDay);
        dataMap.put("u" + uid + "-month", monthVOS);
        dayVOS = gameModeOrderMapper.statisticsByMonthWithDays(uid, firstDay, endDay);
        dataMap.put("u" + uid + "-daily", dayVOS);
      }
      String rkey = RedisConstants.TJ_MONTH_KEY_PREFIX + firstDay.getMonthValue();
      // 入库查询查询数据，存入redis， 供前端使用
      redisUtils.hash_putAll(rkey, dataMap, 31, TimeUnit.DAYS);
    }
  }

  @Override public void statisticsCheckinTimes() {
    String rkey = RedisConstants.TJ_CHECKIN_TIMES_KEY;
    List<PunchSumVO> list = gameModeOrderMapper.statisticsCheckinTimes();
    Map<Long, Integer> dataMap = new HashMap<>();
    if (list != null && !list.isEmpty()) {
      dataMap = list.parallelStream()
          .collect(Collectors.toMap(PunchSumVO::getUid, PunchSumVO::getJoinedRoundsSum,
              (oldValue, newValue) -> oldValue));
    }
    // 入库查询查询数据，存入redis， 供前端使用
    redisUtils.hash_putAll(rkey, dataMap, 31, TimeUnit.DAYS);
  }

  @Override public void statisticsByMonth(LocalDate firstDay, LocalDate currDate) {

    // todo 入库
  }

  private static String yyyyMM(LocalDate localDate) {
    return new StringBuilder().append(localDate.getYear())
        .append(localDate.getMonthValue() >= 10 ? localDate.getMonthValue()
            : "0" + localDate.getDayOfMonth())
        .toString();
  }

  private void settleBenefits(final GameModeOrder order, final GameModeBenefit benefit) {
    // 余额增加金额 & 添加一条记录
    log.info("uid:{} {}倍{}轮：额外奖励{}", order.getUid(), benefit.getPunchAmount(),
        benefit.getPunchRound(), benefit.getBonus());
    if (userMapper.updateBalanceAdd(order.getUid(), benefit.getBonus()) == 1) {
      log.info("uid:{} 额外奖励已发放至余额：{}", order.getUid());
    } else {
      log.info("uid:{} 额外奖励更新钱包失败：{}", order.getUid());
    }
    // 余额增加记录
    BalanceDtl benefitBalanceDtl =
        new BalanceDtl(order.getUid(), "+" + NumberUtil.format3Str(benefit.getBonus()),
            this.buildBenefitDesc(order.getPeriod(), benefit.getPunchAmount(),
                benefit.getPunchRound().intValue()));
    balanceService.addBalanceDtl(benefitBalanceDtl, BalanceDtlTypeCodes._benefit);
  }

  private String buildBenefitDesc(LocalDate period, BigDecimal punchAmount, int punchRound) {
    return new StringBuilder(PunchUtils.punchPeriod(period)).append("期")
        .append(punchAmount.intValue())
        .append("倍")
        .append(punchRound)
        .append("轮")
        .append(WalletDtlTypeCodes._benefit.getDesc())
        .toString();
  }

  @Override public List<GameModeBenefit> getAllAvailableBenefits(GameModeActivityTypeCodes codes) {
    String keys = RedisConstants.GAME_MODE_BENEFIT_KEY_PREFIX + codes.getCode();
    List<GameModeBenefit> benefits = (List<GameModeBenefit>) redisUtils.get(keys);
    if (null == benefits || benefits.isEmpty()) {
      Example example = new Example(GameModeBenefit.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("status", "Y").andEqualTo("type", codes.getCode());
      benefits = gameModeBenefitMapper.selectByExample(example);
      redisUtils.set1Month(keys, benefits);
    }
    return benefits;
  }

  @Override public synchronized boolean triggerPunchFailedByActivity(final GameMode activity) {
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加{}期闯关的人数:{}", activity.getPeriod(), orders.size());
    //  有部分用户是参加了打卡，但是没有进入系统， 所以在活动结束之后 统一触发打卡失败的
    List<GameModeOrder> joiningOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.joining.getCode())
        .collect(Collectors.toList());
    log.info("打卡中的人数:{}", joiningOrders.size());
    // 触发失败
    if (!joiningOrders.isEmpty()) {
      joiningOrders.forEach(punchOrder -> {
        // 查询当前轮
        GameModeRound punchRound =
            this.getPunchRoundByPunchOrder(punchOrder.getUid(), punchOrder.getId(),
                punchOrder.getCurrentRound());
        // fix 失败
        gameModeRoundMapper.updateCheckinStatus(punchOrder.getUid(), punchRound.getId(),
            PunchStatusCodes.fail.getCode());
        // fix 失败
        gameModeOrderMapper.updatePunchStatus(punchOrder.getUid(), punchOrder.getId(),
            PunchStatusCodes.fail.getCode());
        // 活动 失败的总金额， 失败的总人数
        gameModeMapper.updateWhitCheckinFailById(activity.getId(), punchOrder.getAmount());
        // 系统通知
        noticeService.addPunchResultNotice(punchOrder.getUid(), activity,
            punchRound.getRound(), false,
            PunchUtils.buildCheckinTimeForNotice(punchRound.getStartTime(),
                punchRound.getEndTime()));
      });
    }
    return true;
  }
}
