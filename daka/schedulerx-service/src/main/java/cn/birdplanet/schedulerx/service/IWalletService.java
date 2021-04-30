/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: WalletService
 * @date 2019-07-18 14:31
 */
public interface IWalletService {

  /**
   * @param walletDtl
   * @param typeCode
   * @return
   */
  boolean addWalletDtl(WalletDtl walletDtl, WalletDtlTypeCodes typeCode);
}
