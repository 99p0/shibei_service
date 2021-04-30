//package cn.birdplanet.schedulerx.schedule.normalmode;
//
//
//import cn.birdplanet.schedulerx.service.INormalModeService;
//import com.github.pagehelper.PageInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: GenerateNextActivityScheduleTaskForNormalMode
// * @description: 每日创建常规模式活动的定时任务
// * @date 2019/8/29 15:47
// */
//@Slf4j
//@Component
//public class GenerateRegisteringActivityScheduleTask {
//
//  @Autowired private INormalModeService normalModeService;
//
//  /**
//   * 每天00:00:10执行 生成新 常规打卡活动
//   */
//  @Scheduled(cron = "0 0 0 * * ?")
//  public void execute() {
//    log.info("定时创建常规模式打卡任务... start");
//    boolean flag = normalModeService.generateNextPeriodActivityWithTemplate();
//    log.info("定时创建常规模式打卡任务【{}】... end", flag);
//    // 将可报名的数据存放到缓存中
//    log.info("将可报名的数据存放到缓存中... start");
//    PageInfo pageInfo = normalModeService.getPlazaActivities();
//    log.info("将可报名的数据存放到缓存中【{}】... end", pageInfo);
//  }
//}
