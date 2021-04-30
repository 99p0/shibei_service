/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends MrMapper<User> {

  @Update(" UPDATE `t_user` SET `wallet` = `wallet` - #{amount}, `balance` = `balance` + #{amount}  WHERE (`uid` = #{uid})")
  int rechargeBalanceFromWallet(@Param("uid") long uid,
      @Param("amount") BigDecimal amount);

  @Update(" UPDATE `t_user` SET `wallet` = `wallet` - #{amount} WHERE (`uid` = #{uid})")
  int updateWalletSubtract(@Param("uid") long uid,
      @Param("amount") BigDecimal amount);

  @Update(" UPDATE `t_user` SET `wallet` = `wallet` + #{amount} WHERE (`uid` = #{uid})")
  int updateWalletAdd(@Param("uid") long uid, @Param("amount") BigDecimal amount);

  @Select("select * from t_user where (invitation_code = #{code})")
  User getByInvitationCode(@Param("code") String code);

  @Update(" UPDATE `t_user` SET `balance` = `balance` + #{amount} WHERE (`uid` = #{uid})")
  int updateBalanceAdd(@Param("uid") long uid, @Param("amount") BigDecimal amount);

  @Update(" UPDATE `t_user` SET `balance` = `balance` + 1, `is_first_withdraw`= 'N' WHERE (`uid` = #{uid})")
  int updateFirstWithdraw(@Param("uid") long uid);

  @Update(" UPDATE `t_user` SET `balance` = `balance` - #{amount} WHERE (`uid` = #{uid})")
  int updateBalanceSubtract(@Param("uid") long uid,
      @Param("amount") BigDecimal amount);

  @Update(" UPDATE `t_user` SET `moneyqr` = #{moneyQr} WHERE (`uid` = #{uid})")
  int updateMoneyQrByUid(@Param("uid") long uid, @Param("moneyQr") String moneyQr);

  @Update(" UPDATE `t_user` SET `brokerage_sum` = `brokerage_sum` + #{brokerage}, `brokerage`=`brokerage`+#{brokerage} WHERE (`uid` = #{uid})")
  int updateBrokerageAddAndTotalBrokerageAdd(@Param("uid") long uid,
      @Param("brokerage") BigDecimal brokerage);

  @Update(" UPDATE `t_user` SET `balance` = `balance` + #{brokerage}, `brokerage`=`brokerage`-#{brokerage} WHERE (`uid` = #{uid})")
  int updateBalanceAddAndBrokerageSub(@Param("uid") long uid, @Param("brokerage") int brokerage);

  @Update(" UPDATE `t_user` SET `inviter_code` = #{code}, `inviter_uid` = #{inviterUid} WHERE (`uid` = #{uid})")
  int updateInviterCode(@Param("uid") long uid, @Param("code") String code,
      @Param("inviterUid") long inviterUid);

  @Update(" UPDATE `t_user` SET `need_inp_invited_code` = #{status} WHERE (`uid` = #{uid})")
  int updateNotAlertInviteView(@Param("uid") long uid, @Param("status") String status);

  @Update("UPDATE `t_user` SET `nick_name` = #{nickName}, `avatar_path` = #{avatar}, `mobile` = #{mobile}, `gender` = #{gender}, `is_certified` = #{isCertified} WHERE (`uid` = #{uid})")
  int updateUserBaseInfo(@Param("uid") long uid, @Param("nickName") String nickName,
      @Param("avatar") String avatar, @Param("mobile") String mobile,
      @Param("isCertified") String isCertified, @Param("gender") String gender);

  @Update("UPDATE `t_user` SET `brokerage_withdrawal_switch` = #{brokerageSwitch} where `uid` > 100")
  int turnOffBrokerageWithdrawal(@Param("brokerageSwitch") String brokerageSwitch);

  @Update("UPDATE `t_user` SET `brokerage_withdrawal_switch` = #{brokerageSwitch} where (`uid` = #{uid})")
  int turnOffBrokerageWithdrawalByUid(@Param("uid") long uid,
      @Param("brokerageSwitch") String brokerageSwitch);

  @Update("update t_user set brokerage_freeze = brokerage_freeze + brokerage where (uid=#{uid})")
  int freezeBrokerage(@Param("uid") long uid);
}
