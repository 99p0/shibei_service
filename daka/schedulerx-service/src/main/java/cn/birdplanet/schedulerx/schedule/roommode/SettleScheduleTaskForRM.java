package cn.birdplanet.schedulerx.schedule.roommode;

import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.schedulerx.service.IRoomModeService;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: SettleScheduleTaskForRM
 * @description: 每天定时执行结算任务
 * @date 2019/8/29 15:50
 */
@Slf4j
@Component
public class SettleScheduleTaskForRM {

  @Autowired private IRoomModeService roomModeService;

  /**
   * 每天定时执行结算奖金任务
   * <p>
   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
   */
  @Scheduled(cron = "0 30 0 * * ?")
  public void settleBonus() {
    LocalDateTime now = LocalDateTime.now();
    log.info("[{}]系统自动结算房间打卡任务... start", now);
    // 获取所有没有结算的，且活动已经结束
    List<RoomMode> activities = roomModeService.getAllEndAndNotSettledActivities();
    log.info("查询[{}]个房间[活动已经结束且没有结算]", activities.size());
    //
    activities.stream().forEachOrdered(activity -> {
      log.info("房间:: {}", activity);
      // 未结算，且此活动的结算方式为自动结算的
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
        log.info("*** 此房间活动已结算[{}]", activity);
      } else {
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
          log.info("结算房间[{}]。。。start", activity);
          long count = roomModeService.settleRoomActivity(activity);
          log.info("结算房间{}个/[{}]。。。end", count, activity);
        } else {
          log.info("### 此房间活动为手动结算方式[{}]", activity);
        }
      }
    });
    log.info("$$$系统自动结算房间打卡活动本金[{}]的任务... end", now);
  }
}