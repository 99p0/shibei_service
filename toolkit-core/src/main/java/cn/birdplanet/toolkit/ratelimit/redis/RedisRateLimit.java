package cn.birdplanet.toolkit.ratelimit.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RateLimit
 * @description: @RedisRateLimit(key = "test", period = 100, count = 10) 100S 内最多允許訪問10次
 * @date 2019/11/20 15:30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisRateLimit {

  /**
   * 资源的名字
   *
   * @return String
   */
  String name() default "";

  /**
   * 资源的key
   *
   * @return String
   */
  String key() default "";

  /**
   * Key的prefix
   *
   * @return String
   */
  String prefix() default "limit:rate:";

  /**
   * 给定的时间段 单位秒
   *
   * @return int
   */
  int period() default 60;

  /**
   * 最多的访问限制次数
   *
   * @return int
   */
  int count() default 10;

  /**
   * 类型
   *
   * @return LimitType
   */
  RedisRateLimitType limitType() default RedisRateLimitType.IP;
}
