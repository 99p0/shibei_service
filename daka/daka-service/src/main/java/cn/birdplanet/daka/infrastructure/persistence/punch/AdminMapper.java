/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.persistence.punch;

import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.commons.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AdminMapper extends MrMapper<Admin> {

  @Update("UPDATE `t_admin` SET `password` = #{pwd} WHERE (`id` = #{uid})")
  int changeAdminPasswordByUid(@Param("uid") long uid, @Param("pwd") String pwdUseBcrypt);
}
