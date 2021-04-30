/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import cn.birdplanet.daka.domain.po.GameModeGear;
import cn.birdplanet.daka.domain.vo.PunchTimePeriodVO;
import cn.birdplanet.toolkit.PunchUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

@Slf4j
public class PunchingForGMTest {

  /**
   * 获取打卡的时间段 六点之后可以打卡
   *
   * @param round 轮次
   */
  /**
   * 获取打卡的时间段 六点之后可以打卡
   *
   * @param round 轮次
   */
  private static PunchTimePeriodVO getPunchTimePeriod(int round, LocalDateTime activityEndTime,
      String type) {
    // 打卡的时间段
    LocalDateTime checkinStartTime, checkinEndTime, magicalTime,
        // 当前时间  2019-09-17 06:45:00
        currLdt = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(5, 58, 0));
    // 如果当前时间大于，截止时间
    if (currLdt.isAfter(activityEndTime)) {
      throw new RuntimeException("活动已截止，不能再在参加了");
    }
    // 距离结束的分钟数
    int lastMin = (int) (PunchUtils.getPunchingSeconds(currLdt, activityEndTime) / 60);
    log.debug("距离活动截止的分钟数:{}", lastMin);
    if (lastMin <= 120) {
      throw new RuntimeException("此时间段禁止参加打卡");
    }
    // 每轮打卡相关的参数信息
    GameModeGear punchGear = getGearByRound(round, type);

    // 随机范围: 随机在一小时以后的， 如果是早上八点前的两个小时，则需要在此时分配
    int magicalRandomNum = 100;
    int range = RandomUtils.nextInt(magicalRandomNum, punchGear.getRangeMax() - 1);
    log.debug("随机数:{}", range);

    // 如果当前时间在结束前的一个打卡周期150m，活动前一个小时禁止打卡
    // 据结束的分钟数，如果小于周期数， 则已 前者为准
    if (lastMin < punchGear.getRangeMax()) {
      log.debug("据活动截止的分钟数 小于 签到的周期");
      // lastMin >= 120min：50～120-3
      //range = RandomUtils.nextInt(50, lastMin - punchGear.getCheckinPeriod());
       //如果距离结束大于60分钟+打卡周期话
    }
    // 签到的开始时间
    magicalTime = currLdt.plusMinutes(range);
    // 如果随机的时间大于活动截止的时间，重新随机
    if (magicalTime.isAfter(activityEndTime)) {
      log.debug("随机时间[{}] 大于活动截止时间[{}]， 重新随机[{}]", magicalTime, activityEndTime, range);
      getPunchTimePeriod(round, activityEndTime, type);
    }
    // 去除 秒
    LocalTime localTime =
        LocalTime.of(magicalTime.toLocalTime().getHour(), magicalTime.toLocalTime().getMinute());
    checkinStartTime = LocalDateTime.of(magicalTime.toLocalDate(), localTime);
    // 签到结束时间 = 随机的开始时间+ 每一轮登记需要额的时间
    checkinEndTime = checkinStartTime.plusSeconds(punchGear.getTimePeriod());
    log.debug("随机数: {}, 签到时间段:{} ～ {}", range, checkinStartTime, checkinEndTime);
    return new PunchTimePeriodVO(round, checkinStartTime, checkinEndTime);
  }

  /**
   * 获取第几轮次 需要的打卡时间分钟
   */
  private static GameModeGear getGearByRound(int round,String type) {
    GameModeGear punchGear = new GameModeGear();
    punchGear.setId((long) round);
    punchGear.setTimePeriod(180);
    punchGear.setRangeMax(150);
    punchGear.setType(type);
    return punchGear;
  }

  @Test
  public void test() {
    for (int i = 0; i < 1000; i++) {
      LocalDateTime activityEndTime =
          LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7, 59, 59));
      getPunchTimePeriod(1, activityEndTime,"A");
      log.debug("=============");
    }
  }
}
