/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface GameModeOrderMapper extends MrMapper<GameModeOrder> {

  @Update("UPDATE `t_game_mode_order` SET `current_round` = `current_round`+1, `status` = '1' WHERE (`id` = #{id} and `uid` = #{uid})")
  int updateNextRoundById(@Param("id") long id, @Param("uid") long uid);

  @Update("UPDATE `t_game_mode_order` SET `status` = #{status} WHERE (`id` = #{id} and `uid` = #{uid})")
  int updatePunchStatus(@Param("uid") long uid, @Param("id") long id, @Param("status") int status);

  @Update("UPDATE `t_game_mode_order` SET `status` = 2,`joined_rounds` = `joined_rounds`+1 WHERE (`id` = #{id} and `uid` = #{uid})")
  int updatePunchStatusSuccess(@Param("uid") long uid, @Param("id") long id);

  @Select("select uid, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (period >= #{firstDay} and period <= #{lastDay}) group by uid having joinedRoundsSum > ${joinedRoundsSum} order by joinedRoundsSum desc")
  List<PunchSumVO> getPunchSumByMonth_gt(@Param("firstDay") LocalDate firstDay,
      @Param("lastDay") LocalDate lastDay, @Param("joinedRoundsSum") int joinedRoundsSum);

  @Select("select uid, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (period >= #{firstDay} and period <= #{lastDay}) group by uid having joinedRoundsSum < ${joinedRoundsSum} order by joinedRoundsSum desc")
  List<PunchSumVO> getPunchSumByMonth_lt(@Param("firstDay") LocalDate firstDay,
      @Param("lastDay") LocalDate lastDay, @Param("joinedRoundsSum") int joinedRoundsSum);

  @Select("select count(tmo.id) from t_game_mode_order tmo left join t_game_mode tgm on tmo.activity_id = tgm.id where tmo.uid=#{uid} and tmo.period = #{period} and tgm.type != #{code}")
  int getJoinedNumByCondtion(@Param("uid")long uid,@Param("period") LocalDate period, @Param("code")String code);

  @Select("select count(tmo.id) from t_game_mode_order tmo left join t_game_mode tgm on tmo.activity_id = tgm.id where tmo.uid=#{uid} and tmo.period = #{period} and tgm.type in (${codes})")
  int getJoinedNumByUidAndCodes(@Param("uid")long uid,@Param("period") LocalDate period, @Param("codes")String codes);
}
