package cn.birdplanet.schedulerx.schedule.gamemode;

import cn.birdplanet.schedulerx.service.IGameModeService;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: GenerateNextActivityScheduleTaskForGM
 * @description: 每日创建定打卡活动的定时任务
 * @date 2019/8/29 15:47
 */
@Slf4j
@Component
public class GenerateNextActivityScheduleTaskForGM {

  @Autowired private IGameModeService gameModeService;

  /**
   * 每天07:00执行 生成新的活动
   */
  @Scheduled(cron = "0 30 7 * * ?")
  public void second() {
    LocalDate today = LocalDate.now();
    log.info("定时创建今日{}打卡任务... start", today);
    // 根据模版生产新的任务
    boolean flag = gameModeService.generateNextActivityWithTemplate(today);
    log.info("定时创建今日{}闯关任务from Template... {}", today, flag);
  }
}
