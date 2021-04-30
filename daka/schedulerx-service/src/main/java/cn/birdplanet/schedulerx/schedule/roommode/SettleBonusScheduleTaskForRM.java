//package cn.birdplanet.schedulerx.schedule.roommode;
//
//import cn.birdplanet.domain.po.RoomMode;
//import cn.birdplanet.schedulerx.service.IRoomModeService;
//import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: SettleBonusForNormalModeScheduleTask
// * @description: 每天定时执行结算奖金任务
// * @date 2019/8/29 15:50
// */
//@Slf4j
//@Component
//public class SettleBonusScheduleTaskForRM {
//
//  @Autowired private IRoomModeService roomModeService;
//
//  /**
//   * 每天定时执行结算奖金任务
//   *
//   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
//   */
//  @Scheduled(cron = "0 25 0 * * ?")
//  public void settleBonus() {
//    //
//    LocalDateTime now = LocalDateTime.now();
//    log.info("[{}]系统自动结算房间打卡奖金任务... start", now);
//    // 获取所有打卡中或者结束，且没有结算的活动
//    List<RoomMode> activities = roomModeService.getAllEndOrPunchingAndNotSettledActivities();
//    log.info("查询[{}]打卡的任务{} 个 。。。start", now.toLocalDate(), activities.size());
//    //
//    activities.forEach(activity -> {
//      log.info("settleBonus 活动:: {}", activity);
//      // 今天开始的任务不算
//      LocalDateTime yesterday =
//          LocalDateTime.of(now.minusDays(1).toLocalDate(), LocalTime.of(23, 59, 59));
//      if (activity.getStartDatetime().isAfter(yesterday)) {
//        log.info("{}今天开始的活动不参加奖金分配::{}", activity, yesterday);
//        return;
//      }
//      // 未结算，且此活动的结算方式为自动结算的
//      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
//        log.info("!!!!! 此房间打卡已结算[{}]", activity);
//      } else {
//        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
//          log.info("结算房间奖金任务[{}]。。。start", activity.getId());
//          long count = roomModeService.settleBonusForActivity(activity);
//          log.info("结算房间奖金任务{}/{}个。。。end", activity.getId(), count);
//        } else {
//          log.info("### 此活动需要手动结算[{}]", activity);
//        }
//      }
//    });
//    log.info("系统自动结算[{}]的常规打卡。。。end", now);
//  }
//}
