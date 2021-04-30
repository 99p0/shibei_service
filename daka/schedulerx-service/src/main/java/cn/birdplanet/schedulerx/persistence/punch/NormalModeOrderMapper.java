/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.persistence.punch;

import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.schedulerx.common.support.mybatis.MrMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface NormalModeOrderMapper extends MrMapper<NormalModeOrder> {

  @Select("select nmo.`status`,u.`uid`, u.`avatar_path` avatarPath,u.`nick_name` nickName from `t_normal_mode_order` nmo left join `t_user` u on nmo.uid = u.uid where activity_id=#{aid}")
  List<ActivityUserVo> getJoinedUsersByActivityId(@Param("aid") long aid);

  @Update(" UPDATE `t_normal_mode_order` SET `times` = `times`+1 WHERE `id` = #{oid}")
  int addTimesByOrderId(@Param("oid") long oid);

  @Update(" UPDATE `t_normal_mode_order` SET `status` = #{status} WHERE `id` = #{oid}")
  int updatePunchStatusById(@Param("oid") long oid, @Param("status") int status);

  @Select("select o.id, o.uid, o.activity_id activityId, o.period, o.amount, o.multiple, o.times, o.status, o.created_at createdAt, o.updated_at updatedAt from t_normal_mode_order o left join t_normal_mode m on o.activity_id = m.id where (o.uid=#{uid}) and (o.status=1) and (m.start_datetime > now()) order by o.id desc")
  List<NormalModeOrder> getOrdersForRegistered(@Param("uid")long uid);
  @Select("select o.id, o.uid, o.activity_id activityId, o.period, o.amount, o.multiple, o.times, o.status, o.created_at createdAt, o.updated_at updatedAt from t_normal_mode_order o left join t_normal_mode m on o.activity_id = m.id where (o.uid=#{uid}) and (o.status=1) and (m.start_datetime < now()) and (end_datetime > now()) order by o.id desc")
  List<NormalModeOrder> getOrdersForPunching(@Param("uid")long uid);
}
