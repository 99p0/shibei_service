/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface RoomModeMapper extends MrMapper<RoomMode> {

  @Update("UPDATE `t_room_mode` SET `total_amount` = `total_amount`+#{amount}, `total_people` = `total_people`+1 WHERE `id` = #{id}")
  int join(@Param("id") long id, @Param("amount") BigDecimal amount);

  @Update("UPDATE `t_room_mode` SET `fail_total_amount` = `fail_total_amount`+#{amount}, `fail_total_people` = `fail_total_people`+1 WHERE `id` = #{id}")
  int updateWhitCheckinFailById(@Param("id") long id, @Param("amount") BigDecimal amount);

  @Update("UPDATE `t_room_mode` SET `status`=4 WHERE `id` = #{id}")
  int updateActivityComplete(@Param("id") long id);

  @Update("UPDATE `t_room_mode` SET `bonus_pool`=#{bonusPool} WHERE `id` = #{id}")
  int updateBonusPoolById(@Param("id") long id, @Param("bonusPool") BigDecimal bonusPool);

  @Update("UPDATE `t_room_mode` SET `is_settled`='Y' WHERE `id` = #{id}")
  int updateActivitySettleComplete(@Param("id") long id);

  @Update("UPDATE `t_room_mode` SET `total_amount` = `total_amount`+#{totalAmount}, `total_people` = `total_people`+1 WHERE `id` = #{id}")
  int changeNumberForJoin(@Param("id") long id, @Param("totalAmount") BigDecimal totalAmount);

  @Update("UPDATE `t_room_mode` SET `status` = '4' WHERE `status` = '2' and `end_datetime` = #{endTime} ")
  int updateStatusForActivityEnd(@Param("endTime") LocalDateTime endTime);

  @Update("UPDATE `t_room_mode` SET `status` = '2' WHERE `status` = '1' and `start_datetime` = #{startTime}")
  int updateStatusForActivityStart(@Param("startTime") LocalDateTime startTime);

  @Update("UPDATE `t_room_mode` SET `fail_total_amount` = `fail_total_amount`+#{amount}, `fail_total_people` = `fail_total_people`+1 WHERE `id` = #{id}")
  void updatePunchFail(@Param("id") long id, @Param("amount") BigDecimal amount);

  @Update("UPDATE `t_room_mode` SET `is_settled`='Y' WHERE `id` = #{id}")
  int updateSettledComplete(@Param("id") long id);
}
