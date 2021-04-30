/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.BrokerageWithdrawalConditions;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import java.time.LocalDate;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IBrokerageDtlService
 * @date 2019-07-18 03:17
 */
public interface IBrokerageService {

  boolean addWithPunch(long uid, long inviterUid, String inviterNickName, LocalDate period,
      String amount);

  boolean addWithPunch(long uid, long inviteesUid, String inviterNickName, String title,
      String amount);

  boolean turnOffWithdrawal(long uid, YesOrNoCodes yesOrNoCodes);

  int turnOffWithdrawal();

  BrokerageWithdrawalConditions getCondition();

  boolean freezeBrokerageForNotEnoughPunchTotal(User user, PunchSumVO punchSumVO);
}
