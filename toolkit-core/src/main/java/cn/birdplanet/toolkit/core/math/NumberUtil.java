/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.math;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

/**
 * 数值工具类
 */
@Slf4j
public class NumberUtil {

  private NumberUtil() {
  }

  /**
   * 保留小数点后两位
   *
   * @param decimal 数值
   * @return 保留两位后的数值
   */
  public static BigDecimal format_s2(BigDecimal decimal) {
    return format(decimal, 2);
  }

  /**
   * 保留小数点后两三位
   *
   * @param decimal 数值
   * @return 保留三位后的数值
   */
  public static BigDecimal format_s3(BigDecimal decimal) {
    return format(decimal, 3);
  }

  public static BigDecimal format(BigDecimal decimal, int scale) {
    // 精确位数后的数据直接舍掉
    return decimal.setScale(scale, BigDecimal.ROUND_DOWN);
  }

  public static BigDecimal format(BigDecimal decimal, int scale, int roundingMode) {
    return decimal.setScale(scale, roundingMode);
  }

  /**
   * 保留小数点后两位
   *
   * @param decimal 数值
   * @return 保留两位后的数值
   */
  public static String format2Str(BigDecimal decimal) {
    return formatStr(decimal, 2);
  }

  public static String format3Str(BigDecimal decimal) {
    return formatStr(decimal, 3);
  }

  public static String formatStr(BigDecimal decimal, int scale) {
    return format(decimal, scale).toString();
  }
}
