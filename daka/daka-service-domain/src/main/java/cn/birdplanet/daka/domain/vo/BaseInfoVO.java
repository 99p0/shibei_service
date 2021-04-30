/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.daka.domain.po.ServiceQQ;
import cn.birdplanet.daka.domain.po.ServiceWX;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: User
 * @date 2019-06-06 16:19
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfoVO {

  private ServiceWX wx;

  private ServiceQQ qq;

  /** app是否置灰 */
  private Boolean greyscale;

  private String gameRule;
  private String normalRule;
  private String roomRule;

  private String gameGrid;
  private String normalGrid;
  private String roomGrid;

  public BaseInfoVO(ServiceWX wx, ServiceQQ qq) {
    this.wx = wx;
    this.qq = qq;
  }


}
