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
public enum WalletDtlTypeCodes {

  _rechargeToBalance(1, "充值到余额"),
  _withdrawToAlipay(2, "提现到支付宝"),
  _punch(3, "闯关奖励及本金"),
  _PUNCH_RM(8, "房间签到奖励及本金"),
  _PUNCH_RM_OWNER(9, "开房奖励及本金"),
  PUNCH_PRINCIPAL(6, "打卡本金"),
  PUNCH_JL(7, "打卡奖励"),
  _punch_bonus(4, "期保底分红"),
  _benefit(5, "额外奖励"),
  ;

  private final int code;
  private final String desc;

  public static WalletDtlTypeCodes codeOf(int code) {
    for (WalletDtlTypeCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
