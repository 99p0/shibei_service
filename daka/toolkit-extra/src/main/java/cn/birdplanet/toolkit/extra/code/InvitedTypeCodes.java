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
 * @title: BalanceDtlTypeCodes
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum InvitedTypeCodes {

  invite_code(1, "输入邀请码"),
  invite_url(2, "通过邀请链接"),
  ;

  private final int code;
  private final String desc;

  public static InvitedTypeCodes codeOf(int code) {
    for (InvitedTypeCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
