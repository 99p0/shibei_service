package cn.birdplanet.toolkit.ratelimit.guava;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RateLimit
 * @date 2019/11/20 15:30
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuavaRateLimit {

  /**
   * 默认每秒放入桶中的token
   *
   * @return 20D
   */
  double limitNum() default 20;
}
