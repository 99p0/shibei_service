package cn.birdplanet.schedulerx.schedule;

import cn.birdplanet.daka.domain.po.BrokerageWithdrawalConditions;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.schedulerx.common.utils.RedisUtils;
import cn.birdplanet.schedulerx.service.IBrokerageService;
import cn.birdplanet.schedulerx.service.IGameModeService;
import cn.birdplanet.schedulerx.service.IUserService;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 佣金提取设置-定时任务
 *
 * <p>
 * 佣金提取条件：<b>大于150 可提  大于50 小于150 本月不能提 小于50扣除本月佣金</b>
 * </p>
 *
 * <ul>
 * <li>每月月初凌晨，关闭所有的佣金提取功能；</li>
 * <li>每月20号起每天开始计算：如果满足条件，打卡提取的开关</li>
 * <li>每月最后一天执行， 未达标则扣除所有佣金</li>
 * </ul>
 *
 * <p>
 *   Spring默认定时@Scheduled不支持L关键字: 为每个可能的最后几天调用调度程序(28，29，30，31)。然后，在函数块内用if块检查这是否是最后一个日期。如果是，则执行预期的任务。​
 * </p>
 */
@Slf4j
@Component
public class BrokerageWithdrawalScheduleTask {

  @Autowired private IBrokerageService brokerageService;
  @Autowired private IGameModeService gameModeService;
  @Autowired private IUserService userService;
  @Autowired private RedisUtils redisUtils;

  /**
   * 每月1号 00:00:30 执行， 关闭佣金提取功能
   */
  @Scheduled(cron = "0 0 0 1 * ?")
  public void turnOffWithdrawal() {
    log.info("关闭佣金提取功能。。。start");
    // 是否月初第一天
    LocalDate date = LocalDate.now();
    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
    if (!date.isEqual(firstDay)) {
      log.info("关闭佣金提取功能。<当前日期「{}」不是当月第一天「{}」>。。。end", date, firstDay);
      return;
    }
    int count = brokerageService.turnOffWithdrawal();
    log.info("关闭佣金提取「{}」人。。。end ", count);
  }

  /**
   * 每月1号 09:30:00 执行，未达标则扣除所有佣金
   */
  //@Scheduled(cron = "50 59 23 28-31 * ?")
  @Scheduled(cron = "0 30 9 1 * ?")
  public void freezeBrokerageForNotEnoughPunchTotal() {
    log.info("未达标则扣除所有佣金。。。start");
    // 上个月的今天
    LocalDate date = LocalDate.now().minusMonths(1);
    // 月末最后一天
    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
    // 月初第一天
    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
    // 获取佣金条件
    BrokerageWithdrawalConditions conditions = brokerageService.getCondition();
    // 打卡总数小于50的人
    List<PunchSumVO> punchSumVOList =
        gameModeService.getPunchSumByMonthForFreeze(firstDay, lastDay, conditions.getFreezeSum(),
            conditions.getAmountLevel());
    log.info("打卡总数小于{}的数量：{}", conditions.getFreezeSum(), punchSumVOList.size());
    // 没有参加的，去除佣金
    List<PunchSumVO> notJoinList = userService.getNotJoinedByMonth(firstDay, lastDay);
    log.info("未打卡总人数：{}", notJoinList.size());
    // 追加，
    punchSumVOList.addAll(notJoinList);
    log.info("少于{}}打卡总人数：{}", conditions.getFreezeSum(), punchSumVOList.size());
    //
    boolean flag = false;
    // 冻结佣金：累计冻结佣金，「佣金总数不变」 发送通知
    for (PunchSumVO punchSumVO : punchSumVOList) {
      //
      User user = userService.getByUid(punchSumVO.getUid());
      // 佣金大于0的时候进行操作数据库
      if (user.getBrokerage().compareTo(BirdplanetConstants.ZERO_BD) == 1) {
        flag = brokerageService.freezeBrokerageForNotEnoughPunchTotal(user, punchSumVO);
      }
      log.info("》用户:{},当前月闯关总数:{}, 冻结当前佣金：{}, 操作：{}", punchSumVO.getUid(),
          punchSumVO.getJoinedRoundsSum(), user.getBrokerage(), flag);
    }

    log.info("未达标则扣除所有佣金「{}」人。。。end ");
  }

  /**
   * 每月20号到31号凌晨执行，计算当月用户打卡总数，大于150，打开提取的开关
   */
  @Scheduled(cron = "0 0 9 20-31 * ?")
  public void brokerageWithdrawalSwitch() {
    log.info("计算当月用户打卡总数的任务。。。start");
    LocalDate date = LocalDate.now();
    // 月初第一天
    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
    // 月末最后一天
    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
    // 获取佣金条件
    BrokerageWithdrawalConditions conditions = brokerageService.getCondition();
    //
    List<PunchSumVO> punchSumVOList =
        gameModeService.getPunchSumByMonth(firstDay, lastDay, conditions.getCanSum(),
            conditions.getAmountLevel());
    log.info("打卡总数大于150的数量：{}", punchSumVOList.size());
    // 将集合转化成map 存放于redis的map中：
    try {
      Map<Long, Integer> punchSumMap = punchSumVOList.parallelStream().collect(
          Collectors.toMap(PunchSumVO::getUid, PunchSumVO::getJoinedRoundsSum, (k1, k2) -> k1));
      redisUtils.hash_putAll(RedisConstants.PUNCH_SUM_KEY, punchSumMap, 2, TimeUnit.DAYS);
    } catch (Exception e) {
      log.error("打卡总数:将集合转化成map存放于redis的map时ERR", e);
    }
    //
    boolean flag = false;
    String key_d = "";
    for (PunchSumVO punchSumVO : punchSumVOList) {
      try {
        flag = brokerageService.turnOffWithdrawal(punchSumVO.getUid(), YesOrNoCodes.YES);
        log.info("用户:{},当前月闯关总数:{}, 打卡佣金提现开关：{}", punchSumVO.getUid(),
            punchSumVO.getJoinedRoundsSum(), flag);
      } catch (Exception e) {
        log.info("ERROR! punchSumVO:{} /{}", punchSumVO, e);
      }
      if (flag) {
        try {
          key_d = RedisConstants.USER_DETAIL_KEY_PREFIX + punchSumVO.getUid();
          redisUtils.del(key_d);
          log.info("清空redis缓存用户D数据：{}", key_d);
        } catch (Exception e) {
          log.info("ERROR! punchSumVO:{} /清空缓存失败：{}", punchSumVO, key_d);
        }
      }
    }
    log.info("计算当月用户打卡总数的任务。。。end");
  }
}
