/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.persistence.punch;

import cn.birdplanet.daka.domain.po.BrokerageDtl;
import cn.birdplanet.schedulerx.common.support.mybatis.MrMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface BrokerageDtlMapper extends MrMapper<BrokerageDtl> {

  @Update("UPDATE `t_brokerage_dtl` SET `is_read` = '1' WHERE (`id` = #{dtlId}) and (`uid` = #{uid})")
  int updateDtlRead(@Param("uid") long uid, @Param("dtlId") long dtlId);
}
