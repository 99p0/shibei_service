package cn.birdplanet.schedulerx.schedule.gamemode;

import cn.birdplanet.schedulerx.service.IGameModeService;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @description: 闯关模式统计
 */
@Slf4j
@Component
public class StatisticsScheduleTaskForGM {

  @Autowired private IGameModeService gameModeService;

  /**
   * 每月1号03:10执行 统计结算任务
   * <p>
   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
   */
  @Scheduled(cron = "0 10 3 1 * ?")
  public void statisticsByMonth() {
    log.info("统计上个月份的打卡情况:: start");
    LocalDate currDate = LocalDate.now();
    LocalDate firstDay = currDate.with(TemporalAdjusters.firstDayOfMonth());
    if (currDate.isEqual(firstDay)) {
      //
      currDate = currDate.minusMonths(1);
      LocalDate endDay = currDate.with(TemporalAdjusters.lastDayOfMonth());
      gameModeService.statisticsByMonth(currDate, endDay);
    } else {
      log.info("非月初第一天 不执行此任务");
    }
    log.info("统计上个月份的打卡情况:: end");
  }

  /**
   * 每月2-31的 07:40 执行：统计当前月份的打卡轮数情况
   * <p>
   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
   */
  @Scheduled(cron = "0 40 7 2-31 * ?")
  public void statistics() {
    log.info("统计当前月份的打卡轮数情况:: start");
    LocalDate currDate = LocalDate.now();
    LocalDate firstDay = currDate.with(TemporalAdjusters.firstDayOfMonth());
    if (currDate.isEqual(firstDay)) {
      log.info("月初第一天 忽略");
    } else {
      gameModeService.statisticsByMonthWithDay(firstDay, currDate);
    }
    log.info("统计当前月份的打卡情况:: end");
  }

  /**
   * 每天 08:10 执行：统计累计的签到次数
   * <p>
   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
   */
  @Scheduled(cron = "0 10 8 * * ?")
  public void statisticsCheckinTimes() {
    log.info("统计累计的签到次数:: start");
    gameModeService.statisticsCheckinTimes();
    log.info("统计累计的签到次数:: end");
  }
}
