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
 * @title: PayeeAccountTypeCodes
 * @date 2019-07-18 03:04
 */
@ToString
@Getter
@AllArgsConstructor
public enum AlipayOAuthScopeCodes {

  AUTH_USER("auth_user", "主动授权，需要用户点击确认"),
  AUTH_BASE("auth_base", "静默授权");

  private final String code;
  private final String desc;

  public static AlipayOAuthScopeCodes codeOf(String code) {
    for (AlipayOAuthScopeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
