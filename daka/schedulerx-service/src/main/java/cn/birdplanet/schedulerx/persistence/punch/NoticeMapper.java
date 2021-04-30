/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.persistence.punch;

import cn.birdplanet.daka.domain.po.Notice;
import cn.birdplanet.schedulerx.common.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface NoticeMapper extends MrMapper<Notice> {

  @Update("UPDATE `t_notice` SET `is_read` = '1' WHERE (`id` = #{id}) and (`uid` = #{uid})")
  int updateNoticeRead(@Param("uid") long uid, @Param("id") long id);
}
