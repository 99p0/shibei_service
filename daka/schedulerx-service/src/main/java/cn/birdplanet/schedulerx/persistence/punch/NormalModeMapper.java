/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.persistence.punch;

import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.schedulerx.common.support.mybatis.MrMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface NormalModeMapper extends MrMapper<NormalMode> {

  @Update("UPDATE `t_normal_mode` SET `total_amount` = `total_amount`+#{totalAmount}, `total_people` = `total_people`+1 WHERE `id` = #{id}")
  int changeNumberForJoin(@Param("id") long id, @Param("totalAmount") BigDecimal totalAmount);

  @Update("UPDATE `t_normal_mode` SET `status` = '4' WHERE `status` = '2' and `end_datetime` = #{endTime} ")
  int UpdateStatusForActivityEnd(@Param("endTime") LocalDateTime endTime);

  @Update("UPDATE `t_normal_mode` SET `status` = '2' WHERE `status` = '1' and `start_datetime` = #{startTime}")
  int updateStatusForActivityStart(@Param("startTime") LocalDateTime startTime);

  @Update("UPDATE `t_normal_mode` SET `is_settled`='Y' WHERE `id` = #{id}")
  int updateSettledComplete(@Param("id") long id);

  @Update("UPDATE `t_normal_mode` SET `fail_total_amount` = `fail_total_amount`+#{amount}, `fail_total_people` = `fail_total_people`+1 WHERE `id` = #{id}")
  void updatePunchFail(@Param("id") long id,@Param("amount")  BigDecimal amount);
}
