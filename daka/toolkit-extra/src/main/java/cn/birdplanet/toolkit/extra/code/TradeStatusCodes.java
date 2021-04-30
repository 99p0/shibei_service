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
 * @title: TradeStatusCodes
 * @date 2019-07-18 03:04
 */
@Getter
@ToString
@AllArgsConstructor
public enum TradeStatusCodes {

  TRADE_FINISHED("TRADE_FINISHED","交易结束，不可退款"),
  WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),
  TRADE_CLOSED("TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),
  TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),
  ;

  private final String code;
  private final String desc;

  public static TradeStatusCodes codeOf(String code) {
    for (TradeStatusCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }

}
