package cn.birdplanet.daka.infrastructure.commons.util;

import cn.birdplanet.daka.infrastructure.config.BirdplanetConfig;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: EnvUtils
 * @date 2019-06-06 18:58
 */
public class EnvUtils {

  public static boolean isRelease() {
    return BirdplanetConfig.env.equalsIgnoreCase("release");
  }

  public static boolean isDev() {
    return BirdplanetConfig.env.equalsIgnoreCase("dev");
  }

  public static boolean isTest() {
    return BirdplanetConfig.env.equalsIgnoreCase("test");
  }
}
