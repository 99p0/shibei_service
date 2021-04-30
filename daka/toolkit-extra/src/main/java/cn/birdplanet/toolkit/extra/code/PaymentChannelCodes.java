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
@Getter
@ToString
@AllArgsConstructor
public enum PaymentChannelCodes {

  alipay("alipay", "支付宝支付"),
  wxpay("wxpay", "微信支付"),
  ;

  private final String code;
  private final String desc;

  public static PaymentChannelCodes codeOf(String code) {
    for (PaymentChannelCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
