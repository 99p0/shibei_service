/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.BrokerageDtl;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import java.time.LocalDate;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IBrokerageDtlService
 * @date 2019-07-18 03:17
 */
public interface IBrokerageService {

  /**
   * 获取佣金记录明细
   *
   * @param pageNum 页码
   * @param pageSize 数量
   * @param uid 用户UID
   * @return 明细集合
   */
  List<BrokerageDtl> getByUidWithPage(int pageNum, int pageSize, long uid);
  List<BrokerageDtl> getAllWithPage(int pageNum, int pageSize);

  /**
   * 结算佣金到余额
   */
  ActionVo settle(long uid);

  boolean dtlRead(long uid, long dtlId);

  boolean addWithPunch(long uid, long inviterUid, String inviterNickName, LocalDate period,
      String amount);

  boolean addWithPunch(long uid, long inviteesUid, String inviterNickName, String title,
      String amount);

  boolean turnOffWithdrawal(long uid, YesOrNoCodes yesOrNoCodes);

  int turnOffWithdrawal();

  boolean freezeBrokerageForNotEnoughPunchTotal(User user,
      PunchSumVO punchSumVO);
}
