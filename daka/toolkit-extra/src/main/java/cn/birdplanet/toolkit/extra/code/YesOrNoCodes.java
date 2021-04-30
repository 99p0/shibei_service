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
public enum YesOrNoCodes {

  NO("N", "No"),
  YES("Y", "Yes");

  private final String code;
  private final String desc;

  public static YesOrNoCodes codeOf(String code) {
    for (YesOrNoCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
