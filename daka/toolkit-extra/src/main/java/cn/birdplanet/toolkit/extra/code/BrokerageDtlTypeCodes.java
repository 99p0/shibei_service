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
public enum BrokerageDtlTypeCodes {

  inviterPunchSucc(1, "好友成功打卡的佣金"),
  settleToBalance(2, "佣金结算到余额"),
  brokerageFreeze(3, "佣金冻结"),
  ;

  private final int code;
  private final String desc;

  public static BrokerageDtlTypeCodes codeOf(int code) {
    for (BrokerageDtlTypeCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
