/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface GameModeRoundMapper extends MrMapper<GameModeRound> {

  @Update("UPDATE `t_game_mode_round` SET `status` = #{status}, `ip_addr` = #{ipAddr}, `device_type` = #{deviceType}, `device_platform` = #{devicePlatform}, `device_info` = #{deviceInfo}, `location_alipay` = #{locationAlipay}, `checkin_time`= now() WHERE (`id` = #{prid} and uid = #{uid})")
  int updateCheckinStatusSucc(@Param("uid") long uid, @Param("prid") long prid,
      @Param("status") int status,
      @Param("ipAddr") String ipAddr, @Param("deviceType") String deviceType,
      @Param("devicePlatform") String devicePlatform, @Param("deviceInfo") String deviceInfo,
      @Param("locationAlipay") String locationAlipay);

  @Update("UPDATE `t_game_mode_round` SET `status` = #{status} WHERE (`id` = #{prid} and uid = #{uid})")
  int updateCheckinStatus(@Param("uid") long uid, @Param("prid") long prid,
      @Param("status") int status);
}
