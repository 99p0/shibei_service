package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.GameModeBenefit;
import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IPunchService
 * @date 2019-07-08 09:38
 */
public interface IGameModeService {

  List<GameModeOrder> getPunchOrdersByActivityId(long activityId);

  /**
   * 九宫格
   *
   * @return 需要支付金额列表
   */
  List<GameModeGrid9> getGrid9Data(GameModeActivityTypeCodes codes);

  /**
   * 根据打卡的ID获取当前轮次的打卡信息
   */
  GameModeRound getPunchRoundByPunchOrder(long uid, long punchOrderId, int currentRound);

  LocalDate getCurrPeriod();

  /**
   * 根据期数获取 主活动
   */
  List<GameMode> getActivitiesByPeriod(LocalDate period);

  GameMode getActivityById(long aid);

  boolean settle(GameMode activity);
  boolean settleWithJT(GameMode activity);
  boolean settleWithBD(GameMode activity);

  boolean triggerPunchFailedByActivity(GameMode activity);

  List<GameModeBenefit> getAllAvailableBenefits(GameModeActivityTypeCodes codes);

  boolean updateActivityComplete(long aid);

  boolean updateActivitySettle(long aid);

  List<PunchSumVO> getPunchSumByMonth(LocalDate firstDay, LocalDate lastDay, int joinedRoundsSum, BigDecimal amountLevel);

  List<PunchSumVO> getPunchSumByMonthForFreeze(LocalDate firstDay, LocalDate lastDay,
      int joinedRoundsSum, BigDecimal amountLevel);

  boolean generateNextActivityWithTemplate(LocalDate period);

  void statisticsByMonth(LocalDate firstDay, LocalDate endDay);

  void statisticsByMonthWithDay(LocalDate firstDay, LocalDate currDate);

  void statisticsCheckinTimes();
}
