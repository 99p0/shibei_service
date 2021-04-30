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
 * @title: AdminTypeCodes
 * @date 2019-07-18 03:55
 */
@ToString
@Getter
@AllArgsConstructor
public enum AdminTypeCodes {

  _super("super", "超级管理员");

  private final String code;
  private final String desc;

  public static AdminTypeCodes codeOf(String code) {
    for (AdminTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
