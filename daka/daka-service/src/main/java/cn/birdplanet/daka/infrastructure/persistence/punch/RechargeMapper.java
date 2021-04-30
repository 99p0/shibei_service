/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.Recharge;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface RechargeMapper extends MrMapper<Recharge> {

  @Update("UPDATE `t_recharge` SET `trade_status` = #{tradeStatus} WHERE (`uid` = #{uid}) and (`ordersn` = #{ordersn}) and (`trade_no` = #{tradeno})")
  int updateTradeStatus(@Param("uid") long uid, @Param("ordersn") String ordersn,
      @Param("tradeno") String tradeno, @Param("tradeStatus") String tradeStatus);

  @Update("UPDATE `t_recharge` SET `trade_status` = #{tradeStatus} WHERE (`id` = #{id})")
  int updateTradeStatusById(@Param("id") long id, @Param("tradeStatus") String trade_status);

  @Update("UPDATE `t_recharge` SET `trade_status` = #{tradeStatus}, `trade_no` = #{trade_no}, `body_callback` = #{reqParams} WHERE (`id` = #{id})")
  int updateByIdWithRechargeSucc(@Param("id") long id, @Param("trade_no") String trade_no,
      @Param("tradeStatus") String tradeStatus, @Param("reqParams") String reqParams);
}
