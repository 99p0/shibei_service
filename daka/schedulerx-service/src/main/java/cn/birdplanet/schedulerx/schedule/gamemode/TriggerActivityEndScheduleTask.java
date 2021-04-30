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
 * @title: StopActivityScheduleTaskForNormalMode
 * @description: 触发活动结束
 * @date 2019/8/29 15:47
 */
@Slf4j
@Component
public class TriggerActivityEndScheduleTask {

  @Autowired private IGameModeService gameModeService;

  @Scheduled(cron = "58 59 7 * * ?")
  public void execute() {
    log.debug("触发活动结束的任务... start");
    LocalDate yesterday = LocalDate.now().minusDays(1);
    List<GameMode> activities = gameModeService.getActivitiesByPeriod(yesterday);
    if (activities.isEmpty()) {
      log.debug("未查询到有昨天的闯关活动... end");
      return;
    } else {
      activities.forEach(activity -> {
        boolean flag = gameModeService.updateActivityComplete(activity.getId());
        log.debug("触发活动结束的任务{}/{}", flag, activity);
      });
      log.debug("触发活动结束的任务【{}】... end", yesterday);
    }
  }
}
