/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.date.support;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

public class DateTimeFormatterSupport {

  public static final DateTimeFormatter BASIC_ISO_DATE_TIME;
  public static final DateTimeFormatter BASIC_ISO_DATE_TIME_MILLI;
  public static final DateTimeFormatter ISO_LOCAL_DATE_TIME;
  /**
   * 格式"yyyy-MM-dd HH:mm:ss"
   */
  public static final DateTimeFormatter ISO_LOCAL_DATE_TIME_no_mill;
  public static final DateTimeFormatter TIMESTAMP;
  public static final DateTimeFormatter TIMESTAMP_NANO;
  public static final DateTimeFormatter dfb_YYMMDD;

  static {
    BASIC_ISO_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendValue(YEAR, 4)
        .appendValue(MONTH_OF_YEAR, 2)
        .appendValue(DAY_OF_MONTH, 2)
        .appendValue(HOUR_OF_DAY, 2)
        .appendValue(MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendOffset("+HHMMss", "Z")
        .toFormatter();
  }

  static {
    BASIC_ISO_DATE_TIME_MILLI = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendValue(YEAR, 4)
        .appendValue(MONTH_OF_YEAR, 2)
        .appendValue(DAY_OF_MONTH, 2)
        .appendValue(HOUR_OF_DAY, 2)
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendValue(SECOND_OF_MINUTE, 2)
        .appendValue(MILLI_OF_SECOND, 3)
        //.appendValue(NANO_OF_SECOND, 9)
        .optionalStart()
        .appendOffset("+HHMMss", "Z")
        .toFormatter();
  }

  static {
    ISO_LOCAL_DATE_TIME_no_mill = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .toFormatter();
  }

  static {
    ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .toFormatter();
  }

  static {
    TIMESTAMP = new DateTimeFormatterBuilder()
        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .appendValue(MONTH_OF_YEAR, 2)
        .appendValue(DAY_OF_MONTH, 2)
        .appendValue(HOUR_OF_DAY, 2)
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendValue(SECOND_OF_MINUTE, 2)
        .toFormatter();
  }

  static {
    TIMESTAMP_NANO = new DateTimeFormatterBuilder()
        .append(TIMESTAMP)
        .appendFraction(NANO_OF_SECOND, 0, 9, false)
        .toFormatter();
  }

  static {
    dfb_YYMMDD = new DateTimeFormatterBuilder()
        .appendValue(YEAR, 4)
        .appendValue(MONTH_OF_YEAR, 2)
        .appendValue(DAY_OF_MONTH, 2)
        .toFormatter();
  }
}
