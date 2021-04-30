/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchUtils
 * @date 2019-07-19 16:56
 */

@Slf4j
public class PunchUtils {

  private PunchUtils() {
  }

  /**
   * 转化格式 2019-01-01 》0101
   *
   * @param punchDate 打卡日期
   * @return 转换后的格式
   */
  public static String punchPeriod(LocalDate punchDate) {
    String filling = "0";
    int magicalVal = 10;
    int month = punchDate.getMonthValue();
    int days = punchDate.getDayOfMonth();
    StringBuffer dateStrbf = new StringBuffer();
    if (month < magicalVal) {
      dateStrbf.append(filling);
    }
    dateStrbf.append(month);
    if (days < magicalVal) {
      dateStrbf.append(filling);
    }
    dateStrbf.append(days);
    return dateStrbf.toString();
  }

  /**
   * 获取距离打卡的秒数
   *
   * @param punchStartTime 打卡的开始时间
   * @return 秒数
   */
  public static long getPunchingSeconds(LocalDateTime punchStartTime) {
    LocalDateTime now = LocalDateTime.now();
    // 时区
    ZoneOffset zoneOffset = OffsetDateTime.now(ZoneId.systemDefault()).getOffset();
    return punchStartTime.toEpochSecond(zoneOffset) - now.toEpochSecond(zoneOffset);
  }

  /**
   * 获取距离打卡的秒数
   *
   * @param punchStartTime 打卡的开始时间
   * @param punchStartTime 打卡的开始时间
   * @return 秒数
   */
  public static long getPunchingSeconds(LocalDateTime now, LocalDateTime punchStartTime) {
    // 时区
    ZoneOffset zoneOffset = OffsetDateTime.now(ZoneId.systemDefault()).getOffset();
    return punchStartTime.toEpochSecond(zoneOffset) - now.toEpochSecond(zoneOffset);
  }

  public static String buildCheckinTimeForNotice(LocalDateTime startTime, LocalDateTime endTime) {
    return new StringBuilder("签到时间：").append(
        startTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss")))
        .append(" ~ ")
        .append(endTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss")))
        .toString();
  }


  public static String checkNickName(String nickName) {
    return StringUtils.isNotBlank(nickName) ? nickName : "未设置昵称";
  }
}
