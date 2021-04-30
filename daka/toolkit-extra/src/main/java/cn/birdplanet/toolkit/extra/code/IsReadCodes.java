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
 * @title: IsReadCodes
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum IsReadCodes {

  no(0, "未读"),
  yes(1, "已读");

  private final int code;
  private final String desc;

  public static IsReadCodes codeOf(int code) {
    for (IsReadCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
