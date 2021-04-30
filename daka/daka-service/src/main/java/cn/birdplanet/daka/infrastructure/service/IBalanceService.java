/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import java.util.List;

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

  /**
   * 获取余额明细
   *
   * @param pageNum 页码
   * @param pageSize 数量
   * @param uid 用户
   */
  List<BalanceDtl> getByUidWithPage(int pageNum, int pageSize, long uid);

  List<BalanceDtl> getAllWithPage(int pageNum, int pageSize);
}
