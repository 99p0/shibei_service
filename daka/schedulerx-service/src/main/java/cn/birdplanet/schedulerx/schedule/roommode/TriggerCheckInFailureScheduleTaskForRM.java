package cn.birdplanet.schedulerx.schedule.roommode;

import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.schedulerx.common.utils.RedisUtils;
import cn.birdplanet.schedulerx.service.IRoomModeService;
import java.time.LocalDateTime;
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
public class TriggerCheckInFailureScheduleTaskForRM {

  @Autowired private IRoomModeService roomModeService;
  @Autowired protected RedisUtils redisUtils;

  /**
   * 每天23:50执行 触发昨天打卡失败的
   */
  @Scheduled(cron = "0 57 23 * * ?")
  public void triggerPunchFailed() {
    LocalDateTime now = LocalDateTime.now();
    log.info("触发[{}]打卡失败的任务。。。start", now);
    // 获取所有打卡中的活动列表
    List<RoomMode> activities = roomModeService.getAllPunchingActivities();
    log.info("[{}]::查询到{}个打卡中的活动 。。。start", now.toLocalDate(), activities.size());
    //
    for (RoomMode activity : activities) {
      log.info("触发[{}]打卡失败的任务。。。start", activity);
      long count = roomModeService.triggerCheckInFailureByActivity(activity);
      // 更改计算
      if (count > 0L) {
        // 清除缓存，用户获取的时候，再次存入缓存里
        redisUtils.del(roomModeService.getRKeyForActivity(activity.getId()));
      }
      log.info("触发[{}]打卡失败的任务{}。。。end", activity, count);
    }
    log.info("触发[{}]打卡失败的任务。。。end", now);
  }
}
