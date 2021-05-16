/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.BaseInfoVO;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.UserTypeCodes;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IUserService
 * @date 2019-06-06 16:15
 */
public interface IUserService {

  List<User> getAllByPage(int pageNum, int pageSize);

  /**
   * 根据邀请码获取用户信息
   *
   * @param code 邀请码
   * @return 用户
   */
  User getByInvitationCode(String code);

  User getByUidFromRedis(long uid);
  User changeUserCache(long userId);

  User getByUid(long uid);

  String generateInviteCode();

  User getByOpenUid(String openUid, UserTypeCodes alipay);

  boolean uploadMoneyQr(long uid, String moneyQrUrl);

  boolean save(User user);

  boolean updateNotAlertInviteView(long uid, String status);

  boolean updateUserBaseInfo(long uid, String nickName, String avatar,
      String mobile, String isCertified, String gender);

  boolean rechargeSuccess(TradeChannelCodes tradeChannelCode,
      BigDecimal amount, long uid);

  BaseInfoVO getBaseInfo();

  String getBaseInfo_GameRule();
  String getBaseInfo_GameGrid();

  String getBaseInfo_NormalRule();
  String getBaseInfo_NormalGrid();

  String getBaseInfo_RoomRule();
  String getBaseInfo_RoomGrid();

  boolean rechargeByUid(long uid, int amount, String payPwd);
}
