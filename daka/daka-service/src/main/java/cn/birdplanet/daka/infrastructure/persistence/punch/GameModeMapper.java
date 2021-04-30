/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface GameModeMapper extends MrMapper<GameMode> {

  @Update("UPDATE `t_game_mode` SET `total_amount` = `total_amount`+#{amount}, `total_people` = `total_people`+1 WHERE `id` = #{id}")
  int join(@Param("id") long id, @Param("amount") BigDecimal amount);

  @Update("UPDATE `t_game_mode` SET `fail_total_amount` = `fail_total_amount`+#{amount}, `fail_total_people` = `fail_total_people`+1 WHERE `id` = #{id}")
  int updateWhitCheckinFailById(@Param("id") long id, @Param("amount") BigDecimal amount);

  @Update("UPDATE `t_game_mode` SET `status`=${status} WHERE `id` = #{id}")
  int updateActivityStatus(@Param("id") long id, @Param("status") int status);

  @Update("UPDATE `t_game_mode` SET `status`=4, `is_settled`='Y',`bonus_pool`=#{bonusPool},`bonus_pool_real`=#{bonusPoolReal} WHERE `id` = #{id}")
  int updateActivitySettleComplete(@Param("id") long id, @Param("bonusPool") BigDecimal bonusPool,
      @Param("bonusPoolReal") BigDecimal bonusPoolReal);
}
