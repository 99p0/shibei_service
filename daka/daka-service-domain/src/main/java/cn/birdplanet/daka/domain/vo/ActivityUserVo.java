/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUserVo implements Serializable {

  private Long uid;
  private String nickName;
  private String avatarPath;
  private Integer status;

  public ActivityUserVo(long uid, String nickName) {
    this.uid = uid;
    this.nickName= nickName;
    this.status= PunchStatusCodes.success.getCode();
  }
}
