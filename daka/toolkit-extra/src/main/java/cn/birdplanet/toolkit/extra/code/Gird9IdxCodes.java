/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.extra.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: Y or N codes
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum Gird9IdxCodes {

  G1(1, "一分钟"),
  G10(10, "延时"),
  G20(20, "常规"),
  G30(30, "3X1"),
  G40(40, "回血"),
  G50(50, "Yes"),
  G60(60, "Yes"),
  G70(70, "Yes");

  private final int code;
  private final String desc;

  public static Gird9IdxCodes codeOf(int code) {
    for (Gird9IdxCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
