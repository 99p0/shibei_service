/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.schedulerx.persistence.punch.WalletDtlMapper;
import cn.birdplanet.schedulerx.service.IWalletService;
import cn.birdplanet.toolkit.extra.code.IsReadCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: WalletServiceImpl
 * @date 2019-07-18 14:31
 */
@Slf4j
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class WalletServiceImpl extends BaseService implements IWalletService {

  @Autowired private WalletDtlMapper walletDtlMapper;

  @Override public boolean addWalletDtl(WalletDtl walletDtl, WalletDtlTypeCodes typeCode) {
    walletDtl.setType(typeCode.getCode());
    walletDtl.setIsRead(IsReadCodes.no.getCode());
    if (null == walletDtl.getCreatedIn()) {
      walletDtl.setCreatedIn(LocalDateTime.now());
    }
    return walletDtlMapper.insertSelective(walletDtl) == 1;
  }
}
