package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.dto.GameModeDTO;
import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.GameModeBenefit;
import cn.birdplanet.daka.domain.po.GameModeGear;
import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.daka.domain.vo.PunchTimePeriodVO;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.mobile.device.Device;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IPunchService
 * @date 2019-07-08 09:38
 */
public interface IGameModeService {

  List<GameModeOrder> getPunchOrdersByActivityId(long activityId);

  GameModeOrder getOrderByActivityId(long uid, long activityId, boolean useCache);

  /**
   * 九宫格
   *
   * @return 需要支付金额列表
   */
  List<GameModeGrid9> getGrid9Data(GameModeActivityTypeCodes codes);
  List<GameModeGear> getAllGearDataByType(GameModeActivityTypeCodes codes);

  /**
   * 根据打卡的ID获取当前轮次的打卡信息
   */
  GameModeRound getCurrPunchRounds(long uid, long punchId, int currentRound);
  GameModeRound getCurrPunchRoundsForRedis(long uid, long orderId, int currentRound);

  List<PunchTimePeriodVO> join(long uid, GameMode activity, BigDecimal amount, String ipAddr,
      Device device, String deviceInfo, String locationAlipay);

  List<PunchTimePeriodVO> nextRound(long uid, GameModeOrder punchOrder,
      LocalDateTime activityEndTime, String ipAddr, Device device, String deviceInfo, String locationAlipay);

  String checkin(long uid, LocalDateTime punchTime, long aid, String ipAddr, Device device,
      String deviceInfo, String locationAlipay);

  String checkin(long uid, GameMode activity, GameModeOrder punchOrder, LocalDateTime punchTime,
      String ipAddr, Device device, String deviceInfo, String locationAlipay);

  GameModeRound getPunchRoundByPunchOrder(long uid, long id, int currentRound);

  LocalDate getCurrPeriod();

  /**
   * 当前 type为"A"，status为"1"的活动
   */
  GameMode getCurrMainActivity();

  GameMode getCurrActivityByPeriodWithType(LocalDate period, GameModeActivityTypeCodes code);

  /**
   * 根据期数获取 主活动
   */
  List<GameMode> getActivitiesByPeriod(LocalDate period);

  GameMode getActivityById(long aid);

  boolean settle(GameMode activity);

  boolean triggerPunchFailedByActivity(GameMode activity);

  List<GameModeBenefit> getAllAvailableBenefits(GameModeActivityTypeCodes codes);

  List<GameMode> getAllByPage(int pageNum, int pageSize);

  boolean updateActivityComplete(long aid);

  boolean updateActivitySettle(long aid);

  List<PunchSumVO> getPunchSumByMonth(LocalDate firstDay, LocalDate lastDay, int joinedRoundsSum);

  List<PunchSumVO> getPunchSumByMonthForFreeze(LocalDate firstDay, LocalDate lastDay,
      int joinedRoundsSum);

  GameModeDTO gameMode2Dto(GameMode record);
  List<GameModeDTO> getAvailableActivities(LocalDate currPeriod, boolean redisCache);

  boolean generateNextActivityWithTemplate(LocalDate period);

  void statisticsByMonth(String y_month);

  boolean isJoinedOthers(long uid, LocalDate period, GameModeActivityTypeCodes typeCodes);
  boolean isJoinedOthers(long uid, LocalDate period, String typeCodes);
}
