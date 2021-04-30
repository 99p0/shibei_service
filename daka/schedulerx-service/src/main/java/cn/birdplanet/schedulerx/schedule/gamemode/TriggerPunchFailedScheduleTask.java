package cn.birdplanet.schedulerx.schedule.gamemode;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.schedulerx.service.IGameModeService;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: TriggerPunchFailedScheduleTask
 * @description: 触发每天打卡失败的
 * @date 2019/8/29 15:50
 */
@Slf4j
@Component
public class TriggerPunchFailedScheduleTask {

  @Autowired private IGameModeService gameModeService;

  /**
   * 每天08:01执行 触发昨天打卡失败的
   */
  @Scheduled(cron = "0 1 8 * * ?")
  public void triggerPunchFailed() {
    // 昨天的活动
    LocalDate yesterday = LocalDate.now().minusDays(1);
    log.info("触发[{}]打卡失败的任务。。。start", yesterday);
    List<GameMode> activities = gameModeService.getActivitiesByPeriod(yesterday);
    activities.forEach(activity -> {
      boolean flag = gameModeService.triggerPunchFailedByActivity(activity);
      log.info("触发[{}]打卡失败的任务: {}/{}", yesterday, flag, activity);
    });
    log.info("触发[{}]打卡失败的任务 end", yesterday);
  }
}
