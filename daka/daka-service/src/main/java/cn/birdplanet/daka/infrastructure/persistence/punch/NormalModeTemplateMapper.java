/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.time.LocalDate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface NormalModeTemplateMapper extends MrMapper<NormalModeTemplate> {

  @Update("UPDATE `t_normal_mode_template` SET `last_generate_period` = #{period} WHERE (`id` = #{id})")
  int updateLastPeriodById(@Param("id") long id, @Param("period") LocalDate period);

  @Update("UPDATE `t_normal_mode_template` SET `status` = 2 WHERE (`id` = #{id})")
  int delById(@Param("id") long id);

  @Update("UPDATE `t_normal_mode_template` SET `time_change_last` = #{tcl} WHERE (`id` = #{id})")
  int updateLastTimeChangeById(@Param("id") long id, @Param("tcl") String tcl);
}
