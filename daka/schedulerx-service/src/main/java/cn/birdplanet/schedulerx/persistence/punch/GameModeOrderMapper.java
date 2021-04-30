/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.persistence.punch;

import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthVO;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthWithDayVO;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.schedulerx.common.support.mybatis.MrMapper;
import java.math.BigDecimal;
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

  @Select("select uid, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (amount >= #{amountLevel}) and (period >= #{firstDay} and period <= #{lastDay}) group by uid having joinedRoundsSum > ${joinedRoundsSum} order by joinedRoundsSum desc")
  List<PunchSumVO> getPunchSumByMonth_gt(@Param("firstDay") LocalDate firstDay,
      @Param("lastDay") LocalDate lastDay, @Param("joinedRoundsSum") int joinedRoundsSum,@Param("amountLevel") BigDecimal amountLevel);

  @Select("select uid, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (amount >= #{amountLevel}) and (period >= #{firstDay} and period <= #{lastDay}) group by uid having joinedRoundsSum < ${joinedRoundsSum} order by joinedRoundsSum desc")
  List<PunchSumVO> getPunchSumByMonth_lt(@Param("firstDay") LocalDate firstDay,
      @Param("lastDay") LocalDate lastDay, @Param("joinedRoundsSum") int joinedRoundsSum,
      @Param("amountLevel") BigDecimal amountLevel);

  @Select("select amount, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (period >= #{firstDay} and period <= #{lastDay}) and uid =#{uid} group by amount")
  List<PunchJoinRoundSumByMonthVO> statisticsByMonth(@Param("uid") long uid,
      @Param("firstDay") LocalDate firstDay, @Param("lastDay") LocalDate lastDay);

  @Select("select period, amount, sum(joined_rounds) joinedRoundsSum from t_game_mode_order where (period >= #{firstDay} and period <= #{lastDay}) and uid =#{uid} group by amount,period")
  List<PunchJoinRoundSumByMonthWithDayVO> statisticsByMonthWithDays(@Param("uid") long uid,
      @Param("firstDay") LocalDate firstDay, @Param("lastDay") LocalDate lastDay);

  @Select("select distinct uid from t_game_mode_order where (period >= #{firstDay} and period <= #{lastDay})")
  List<Long> getUidsWithJoinedInCurrDate(@Param("firstDay") LocalDate firstDay, @Param("lastDay") LocalDate lastDay);

  @Select("select tu.uid,  IFNULL(sum(tmo.joined_rounds), 0) joinedRoundsSum from t_user tu left join t_game_mode_order tmo  on tmo.uid = tu.uid group by tu.uid")
  List<PunchSumVO> statisticsCheckinTimes();
}
