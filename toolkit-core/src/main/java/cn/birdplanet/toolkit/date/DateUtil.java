/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.date;

import cn.birdplanet.toolkit.date.support.DateTimeFormatterSupport;
import com.google.common.base.Strings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class DateUtil {

  private DateUtil() {
  }

  /**
   * 获取用户的年龄
   *
   * @param birthday 出生日期
   * @param nowDate  如果为空的话》默认当前时间
   */
  public static int getAge(String birthday, String nowDate) {
    return getAge(LocalDate.parse(birthday),
        StringUtils.isNotBlank(nowDate) ? LocalDate.parse(nowDate) : LocalDate.now());
  }

  /**
   * 截止到当前时间 该用户的年龄
   *
   * @param birthday 用户的出生日期
   */
  public static int getAge(String birthday) {
    return getAge(birthday, null);
  }

  /**
   * 获取用户的年龄
   *
   * @param birthday 出生日期
   * @param nowDate  如果为空的话》默认当前时间
   */
  public static int getAge(LocalDate birthday, LocalDate nowDate) {
    Period period = Period.between(birthday, nowDate);
    return period.getYears();
  }

  /**
   * 两个时间相差的天数
   *
   * @param startDate yyyy-MM-dd
   * @param endDate   yyyy-MM-dd
   */
  public static int getDays(String startDate, String endDate) {
    try {
      return daysBetween(startDate, endDate);
    } catch (Exception e) {
      //            logger.error("计算日期出错!",e);
      return 0;
    }
  }

  public static long getDays(LocalDate startDate, LocalDate endDate) {
    try {
      return endDate.toEpochDay() - startDate.toEpochDay();
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * 获取时间戳
   *
   * @return 201803061607
   */
  public static String getTimestamp() {
    return getTimestamp(null);
  }

  /**
   * 获取时间戳
   *
   * @return 20180306160813577
   */
  public static String getTimestamp_milli() {
    return LocalDateTime.now()
        .format(DateTimeFormatterSupport.BASIC_ISO_DATE_TIME_MILLI);
  }

  /**
   * 获取时间戳
   */
  public static String getTimestamp(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      localDateTime = LocalDateTime.now();
    }
    return localDateTime.format(DateTimeFormatterSupport.BASIC_ISO_DATE_TIME);
  }

  private static int daysBetween(String smdate, String bdate) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.setTime(sdf.parse(smdate));
    long time1 = cal.getTimeInMillis();
    cal.setTime(sdf.parse(bdate));
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / (1000 * 3600 * 24);
    return Integer.parseInt(String.valueOf(between_days)) + 1;
  }

  /**
   * 日期格式化 yyyy.MM.dd
   *
   * @param datetime 需要格式化的日期
   * @return 格式化后的日期
   */
  public static String dateFormat(LocalDateTime datetime) {
    return datetime.getYear() + "." + datetime.getMonthValue() + "." + datetime.getDayOfMonth();
  }

  public static String yyyyMM(LocalDate localDate) {
    return new StringBuilder().append(localDate.getYear())
        .append(localDate.getMonthValue() >= 10 ? localDate.getMonthValue()
            : "0" + localDate.getDayOfMonth())
        .toString();
  }

  /**
   * 日期格式化 yyyy.MM.dd
   *
   * @param datetime 需要格式化的日期
   * @return 格式化后的日期
   */
  public static String dateF_all(LocalDateTime datetime) {
    if (null == datetime) {
      datetime = LocalDateTime.now();
    }
    return datetime.getYear()
        + datetime.getMonthValue()
        + datetime.getDayOfMonth()
        + datetime.getHour()
        + datetime.getMinute()
        + datetime.getSecond()
        + datetime.getNano()
        + "";
  }

  public static String dateF_ymd(LocalDateTime datetime) {
    if (null == datetime) {
      datetime = LocalDateTime.now();
    }
    return datetime.getYear()
        + datetime.getMonthValue()
        + datetime.getDayOfMonth()
        + "";
  }

  public static String dateF_ymd(String date) {
    LocalDate localDate;
    if (Strings.isNullOrEmpty(date)) {
      String[] sp = date.split("-");
      localDate =
          LocalDate.of(Integer.valueOf(sp[0]), Integer.valueOf(sp[1]), Integer.valueOf(sp[2]));
    } else {
      localDate = LocalDate.now();
    }
    return localDate.getYear()
        + localDate.getMonthValue()
        + localDate.getDayOfMonth()
        + "";
  }

  public static String dateF_all() {
    return dateF_all(LocalDateTime.now());
  }

  public static boolean isDate(String date) {
    String rexp =
        "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    Pattern pat = Pattern.compile(rexp);
    Matcher mat = pat.matcher(date);
    boolean dateType = mat.matches();
    return dateType;
  }

  /**
   * 是否已结算
   *
   * @param orderDate 保单日期
   * @return true：账单日之前「已结算」 false：账单日之后「本月账单」
   */
  public static boolean isSettBill(LocalDateTime orderDate) {
    // 当前月的第一天
    LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
    return orderDate.isBefore(firstDayOfMonth);
  }

  public static String timeStampToStr(long timestamp) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Long time = new Long(timestamp);
    String d = format.format(time);
    return d;
  }

  public static String _sdf(LocalDateTime ldt) {
    return _sdf(DateTimeFormatterSupport.ISO_LOCAL_DATE_TIME_no_mill, ldt);
  }

  public static String _sdf() {
    return _sdf(DateTimeFormatterSupport.ISO_LOCAL_DATE_TIME_no_mill, LocalDateTime.now());
  }

  public static String _sdf(DateTimeFormatter dtf, LocalDateTime ldt) {
    return dtf.format(ldt);
  }

  public static String _sdf(DateTimeFormatter dtf, LocalDate ld) {
    return dtf.format(ld);
  }

  public static LocalDateTime ms2Localdatetime(long milliseconds) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds),
        Clock.systemDefaultZone().getZone());
  }

  /**
   * 计算当前日期与{@code endDate}的间隔天数
   *
   * @return 间隔天数
   */
  public static long until(LocalDate endDate) {
    return LocalDate.now().until(endDate, ChronoUnit.DAYS);
  }

  /**
   * 计算日期{@code startDate}与{@code endDate}的间隔天数
   *
   * @return 间隔天数
   */
  public static long until(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate, ChronoUnit.DAYS);
  }
}
