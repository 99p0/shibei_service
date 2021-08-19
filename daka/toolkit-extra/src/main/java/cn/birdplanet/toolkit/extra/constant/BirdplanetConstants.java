package cn.birdplanet.toolkit.extra.constant;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: BirdplanetConstants
 * @date 2019/9/20 08:57
 */
public class BirdplanetConstants {

  public static final long ONE_DAY_MILLISECONDS = 1000 * 60 * 60 * 24L;

  public static final BigDecimal BD1 = new BigDecimal("1.00");
  public static final BigDecimal ZERO_BD = new BigDecimal("0");
  public static final BigDecimal BD001 = new BigDecimal("0");

  public static final LocalTime ZERO_TIME = LocalTime.of(0, 0, 0);
  public static final LocalTime DEF_PERIOD_START_TIME = LocalTime.of(8, 0, 0);
  public static final LocalTime DEF_PERIOD_END_TIME = LocalTime.of(7, 59, 59);

  /**
   * 提现手续费
   */
  public static final BigDecimal WITHDRAWAL_FEE = new BigDecimal("0.01");
  /**
   * 提现的限额
   */
  public static final BigDecimal WITHDRAWAL_LIMIT = new BigDecimal("49.00");

  public static final String POSTER_URL_PREFIX = "https://www.zhuomuniaodaka.com/poster/";
  public static final String POSTER_INVITE_PATH = "/data/birdplanet/moneyqr/shibei/";

  public static final String AVATAR_DEF_URL = "https://www.zhuomuniaodaka.com/shibei/default.png";


  public static final String TOKEN_KEY = "Authorization";
  public static final String TOKEN_VALUE_PREFIX = "Bearer ";

  public static final long UPLOAD_FILE_MAX10MB = 1048576 * 10;

  public static final LocalTime LOCAL_TIME_00_00_00 = LocalTime.of(0, 0, 0);
  public static final LocalTime LOCAL_TIME_23_59_59 = LocalTime.of(23, 59, 59);
}
