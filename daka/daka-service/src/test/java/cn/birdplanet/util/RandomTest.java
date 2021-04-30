/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

@Slf4j
public class RandomTest {

  @Test
  public void test1() {
    System.out.println(RandomStringUtils.random(32, "cvbnm"));// 自定义随机的字符
    System.out.println(RandomStringUtils.randomAlphabetic(32)); // 字母
    System.out.println(RandomStringUtils.randomAlphanumeric(32)); // 字母数字
    System.out.println(RandomStringUtils.randomNumeric(32)); // 数字
  }

  @Test
  public void testRandomInt() {
    for (int i = 0; i < 10000; i++) {
      System.out.println(i + " :: " + RandomUtils.nextInt(5, 58));
    }
  }
}
