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
// * @title: Registering2PunchingScheduleTask
// * @description: 活动状态 》更新已开始打卡的活动
// * @date 2019/8/29 15:47
// */
//@Slf4j
//@Component
//public class Registering2PunchingScheduleTask {
//
//  @Autowired private INormalModeService normalModeService;
//
//  /**
//   * 更新已开始打卡的活动
//   */
//  @Scheduled(cron = "1 0 0 * * ?")
//  public void execute() {
//    LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
//    log.info("常规活动开始任务[{}]... start", startTime);
//    int countNum = normalModeService.updateStatusForActivityStart(startTime);
//    log.info("常规活动开始任务[{}]/{}... end", startTime, countNum);
//  }
//}
