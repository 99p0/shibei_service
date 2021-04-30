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
 * @title: UidCode
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum FundsTypeCodes {

  balance(2, "余额"),
  wallet(1, "钱包");

  private final int code;
  private final String desc;

  public static FundsTypeCodes codeOf(int code) {
    for (FundsTypeCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }

}
