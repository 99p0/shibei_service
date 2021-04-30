/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.GameModeTemplate;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.time.LocalDate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface GameModeTemplateMapper extends MrMapper<GameModeTemplate> {

  @Update("UPDATE `t_game_mode_template` SET `last_generate_period` = #{period} WHERE `id` = #{id}")
  int updateLastPeriodById(@Param("id") long id, @Param("period") LocalDate period);
}
