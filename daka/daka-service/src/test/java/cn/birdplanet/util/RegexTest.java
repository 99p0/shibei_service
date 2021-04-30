/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RegexTest {

  @Test
  public void regexMobile() {
    String reg = "^(((1[3|4|5|7|8|9][0-9]))[0-9]{8})$";
    String mobile = "18108631862";
    if (Pattern.matches(reg, mobile)) {
      System.out.println("true");
    } else {
      System.out.println("false");
    }
  }
}
