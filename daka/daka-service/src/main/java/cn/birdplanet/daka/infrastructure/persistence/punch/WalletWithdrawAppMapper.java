/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.WalletWithdrawApp;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface WalletWithdrawAppMapper extends MrMapper<WalletWithdrawApp> {

  @Update("UPDATE `t_wallet_withdraw_app` SET `status` = #{status}, `remark` = #{remark}, `transfer_at` = now() WHERE `id` = #{id}")
  int confirmTransfer(@Param("id") long id, @Param("status") int status,
      @Param("remark") String remark);
}
