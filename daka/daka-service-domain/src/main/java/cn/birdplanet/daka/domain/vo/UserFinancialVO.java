/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;


import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: User
 * @date 2019-06-06 16:19
 */
@Data
@Slf4j
@NoArgsConstructor
public class UserFinancialVO {

  private String balance;
  private String wallet;
  private String brokerage;
  private String brokerageSum;

  private String moneyqr;

  private Boolean isFirstWithdraw;
  private Boolean isTodayWithdraw;

  public UserFinancialVO(User userInfo) {
    this.moneyqr = userInfo.getMoneyqr();
    this.balance = String.valueOf(userInfo.getBalance().intValue());
    this.wallet = NumberUtil.format3Str(userInfo.getWallet());
    this.brokerage = NumberUtil.format3Str(userInfo.getBrokerage());
    this.brokerageSum = NumberUtil.format3Str(userInfo.getBrokerageSum());
    this.isFirstWithdraw = YesOrNoCodes.YES.getCode().equalsIgnoreCase(userInfo.getIsFirstWithdraw());
  }
}
