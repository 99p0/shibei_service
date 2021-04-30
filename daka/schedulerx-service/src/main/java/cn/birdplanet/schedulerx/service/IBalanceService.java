/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IBalanceService
 * @date 2019-07-18 03:18
 */
public interface IBalanceService {

  /**
   * 添加余额明细
   *
   * @param balanceDtl 资金po
   * @return 是否添加成功
   */
  boolean addBalanceDtl(BalanceDtl balanceDtl, BalanceDtlTypeCodes typeCode);
}
