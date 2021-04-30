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
public enum AlipayAccountTypeCodes {

  USERID("ALIPAY_USERID", "支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。"),
  LOGONID("ALIPAY_LOGONID", "支付宝登录号，支持邮箱和手机号格式。");

  private final String code;
  private final String desc;

  public static AlipayAccountTypeCodes codeOf(String code) {
    for (AlipayAccountTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
