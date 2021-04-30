package cn.birdplanet.schedulerx.schedule.gamemode;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.schedulerx.service.IGameModeService;
import cn.birdplanet.toolkit.extra.code.SettleTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: SettleMainActivityScheduleTask
 * @description: 结算闯关模式 当前活动
 * @date 2019/8/29 15:50
 */
@Slf4j
@Component
public class SettleScheduleTaskForGM {

  @Autowired private IGameModeService gameModeService;

  /**
   * 每天08:30执行 计结算任务
   * <p>
   * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
   */
  @Scheduled(cron = "0 30 8 * * ?")
  public void settleYesterdayActivity() {
    // 结算昨天的活动
    LocalDate yesterday = LocalDate.now().minusDays(1);
    log.info("系统自动结算{}期的闯关... start", yesterday);
    List<GameMode> activities = gameModeService.getActivitiesByPeriod(yesterday);
    if (activities.isEmpty()) {
      log.info("{}期的闯关，活动不存在", yesterday);
      return;
    }
    //
    for (GameMode activity : activities) {
      // 未结算，且此活动的结算方式为自动结算的
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
        log.info("!!!!! 此闯关已结算[{}]", activity);
        continue;
      } else {
        if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
          boolean flag;
          if (activity.getSettleType().equalsIgnoreCase(SettleTypeCodes.JT.getCode())) {
            flag = gameModeService.settleWithJT(activity);
          } else if (activity.getSettleType().startsWith("BD")) {
            // 保底模式：含0元保底，
            flag = gameModeService.settleWithBD(activity);
          } else {
            flag = gameModeService.settle(activity);
          }
          log.info("系统自动结算{}期/{}的闯关{}... end", yesterday, activity.getId(), flag);
        } else {
          log.info("### 此活动需要手动结算[{}]", activity);
        }
      }
    }
  }
}
