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
public enum UserStatusCodes {

  normal(1, "正常"),
  closed(2, "锁定");

  private final int code;
  private final String desc;

  public static UserStatusCodes codeOf(int code) {
    for (UserStatusCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }

}
