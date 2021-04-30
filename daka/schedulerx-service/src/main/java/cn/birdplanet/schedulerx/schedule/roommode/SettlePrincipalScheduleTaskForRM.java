//package cn.birdplanet.schedulerx.schedule.roommode;
//
//import cn.birdplanet.domain.po.RoomMode;
//import cn.birdplanet.schedulerx.service.IRoomModeService;
//import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: SettlePrincipalForNormalModeScheduleTask
// * @description: 每天定时执行结算本金任务
// * @date 2019/8/29 15:50
// */
//@Slf4j
//@Component
//public class SettlePrincipalScheduleTaskForRM {
//
//  @Autowired private IRoomModeService roomModeService;
//
//  /**
//   * 每天定时执行结算本金任务
//   *
//   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
//   */
//  @Scheduled(cron = "1 10 1 * * ?")
//  public void settlePrincipal() {
//    //
//    LocalDateTime now = LocalDateTime.now();
//    log.info("$$$系统自动结算房间活动本金[{}]的任务... start", now);
//    // 获取所有没有结算的，且活动已经结束
//    List<RoomMode> activities = roomModeService.getAllEndAndNotSettledActivities();
//    log.info("查询[{}]个活动[没有结算且活动已经结束]", activities.size());
//    //
//    activities.stream().forEachOrdered(activity -> {
//      log.info("活动:: {}", activity);
//      // 未结算，且此活动的结算方式为自动结算的
//      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
//        log.info("*** 此房间活动已结算[{}]", activity);
//      } else {
//        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
//          log.info("结算房间活动本金任务[{}]。。。start", activity);
//          long count = roomModeService.settlePrincipalForActivity(activity);
//          log.info("结算房间活动本金任务 {}个/[{}]。。。end", count, activity);
//        } else {
//          log.info("### 此房间活动为手动结算方式[{}]", activity);
//        }
//      }
//    });
//    log.info("$$$系统自动结算房间打卡活动本金[{}]的任务... end", now);
//  }
//}
