//package cn.birdplanet.schedulerx.schedule.normalmode;
//
//import cn.birdplanet.schedulerx.service.INormalModeService;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: StopActivityScheduleTaskForNormalMode
// * @description: 活动状态 》更新已结束的活动
// * @date 2019/8/29 15:47
// */
//@Slf4j
//@Component
//public class Punching2EndScheduleTask {
//
//  @Autowired private INormalModeService normalModeService;
//
//  /**
//   * 每天 23:59:59 结束常规打卡活动
//   */
//  @Scheduled(cron = "55 59 23 * * ?")
//  public void execute() {
//    LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
//    log.info("结束常规打卡的任务[{}]... start", endTime);
//    int countNum = normalModeService.updateStatusForActivityExpired(endTime);
//    log.info("结束常规打卡的任务【{}】/{}... end", endTime, countNum);
//  }
//}
