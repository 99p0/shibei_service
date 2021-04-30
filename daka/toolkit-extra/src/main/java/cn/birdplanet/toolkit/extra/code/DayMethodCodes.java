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
 * @title: BonusMethodCodes
 * @description: R费率，FA固定金额
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum DayMethodCodes {

  FA("FA", "固定金额"),
  R("R", "费率");

  private final String code;
  private final String desc;

  public static DayMethodCodes codeOf(String code) {
    for (DayMethodCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
