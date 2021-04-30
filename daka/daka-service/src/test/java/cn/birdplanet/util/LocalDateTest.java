/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LocalDateTest {

  LocalDate birthday = LocalDate.of(2017, 8, 25);
  LocalDate now = LocalDate.now();

  @Test
  public void testDuration() {
    Instant inst1 = Instant.now();
    Instant inst2 = inst1.plus(Duration.ofSeconds(10));
    Duration duration = Duration.between(inst1, inst2);
    log.debug("相差{}秒", duration.getSeconds());
    log.debug("相差{}毫秒", duration.toMillis());
    log.debug("相差{}小时", duration.toHours());
    log.debug("相差{}天", duration.toDays());
    log.debug("相差{}纳秒", duration.toNanos());
  }

  @Test
  public void testDays() {
    Period period = Period.between(birthday, now);
    log.debug("{}年{}月{}天", period.getYears(), period.getMonths(), period.getDays());
    // 间隔天数
    long periodDay = now.toEpochDay() - birthday.toEpochDay();
    log.debug("间隔{}天 - Period", periodDay);

    long daysdiff = ChronoUnit.DAYS.between(birthday, now);
    log.debug("间隔{}天 - ChronoUnit", daysdiff);

    long days = birthday.until(now, ChronoUnit.DAYS);
    log.debug("间隔{}天 - until", days);
  }

  @Test
  public void test() {
    LocalDate dateOfBirth = LocalDate.of(2020, 8, 25);
    if (dateOfBirth.isLeapYear()) {
      System.out.println(dateOfBirth + "是闰年");
    }
    MonthDay birthday = MonthDay.of(dateOfBirth.getMonth(), dateOfBirth.getDayOfMonth());
    MonthDay currentMonthDay = MonthDay.from(LocalDate.now());
    if (currentMonthDay.equals(birthday)) {
      System.out.println("Many Many happy returns of the day !!");
    } else {
      System.out.println("Sorry, today is not your birthday");
    }
    Instant timestamp = Instant.now();
    System.out.println("What is value of this instant " + timestamp);
  }

  @Test
  public void testSout() {
    LocalDate today = LocalDate.now();
    int year = today.getYear();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();
    System.out.printf("Year : %d Month : %d day : %d  %n", year, month, day);

    //%s 字符串类型  "mingrisoft"
    //%c 字符类型 'm'
    //%b 布尔类型 true
    //%d 整数类型（十进制） 99
    //%x 整数类型（十六进制） FF
    //%o 整数类型（八进制） 77
    //%f 浮点类型 99.99
    //%a 十六进制浮点类型 FF.35AE
    //%e 指数类型 9.38e+5
    //%g 通用浮点类型（f和e类型中较短的）
    //%h 散列码
    //%% 百分比类型
    //%n 换行符
    //%tx 日期与时间类型（x代表不同的日期与时间转换符
  }
}
