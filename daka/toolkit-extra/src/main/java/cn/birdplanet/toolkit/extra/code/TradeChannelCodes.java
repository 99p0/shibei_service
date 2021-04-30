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
 * @title: TradeChannelCodes
 * @date 2019-07-18 03:04
 */
@Getter
@ToString
@AllArgsConstructor
public enum TradeChannelCodes {

  wallet("wallet", "钱包"),
  alipay("alipay", "支付宝"),
  wxpay("wxpay", "微信");

  private final String code;
  private final String desc;

  public static TradeChannelCodes codeOf(String code) {
    for (TradeChannelCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
