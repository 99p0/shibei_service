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
public enum BalanceDtlTypeCodes {

  _inputInviteCode(1, "输入邀请码"),
  _settleBrokerage(2, "提取拥金"),
  _friendFirstWithdraw(3, "好友首次提现奖励"),
  _firstWithdraw(4, "首次提现奖励"),
  _rechargeWithAlipay(5, "支付宝充值"),
  _rechargeWithWallet(6, "钱包充值"),
  _join(7, "加入闯关"),
  _ActivityAward(8, "活动奖励"),
  _benefit(9, "额外奖励"),
  _rechargeWithConsole(11, "后台充值金额"),
  _rechargeFxWithConsole(12, "后台充值活动返现"),
  ;

  private final int code;
  private final String desc;


  public static BalanceDtlTypeCodes codeOf(int code) {
    for (BalanceDtlTypeCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }

}
