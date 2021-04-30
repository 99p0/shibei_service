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
 * @description: 奖金分配方式，A均分，B保底，R随机
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum BonusMethodCodes {

  A("A", "均分"),
  B("B", "保底"),
  R("R", "随机");

  private final String code;
  private final String desc;

  public static BonusMethodCodes codeOf(String code) {
    for (BonusMethodCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
