/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.schedulerx.persistence.punch.BalanceDtlMapper;
import cn.birdplanet.schedulerx.service.IBalanceService;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.IsReadCodes;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: BalanceServiceImpl
 * @date 2019-07-18 03:19
 */
@Slf4j
@Service
public class BalanceServiceImpl extends BaseService implements IBalanceService {

  @Autowired private BalanceDtlMapper balanceDtlMapper;

  @Override public boolean addBalanceDtl(BalanceDtl balanceDtl, BalanceDtlTypeCodes typeCode) {
    balanceDtl.setType(typeCode.getCode());
    balanceDtl.setIs_read(IsReadCodes.no.getCode());
    if (null == balanceDtl.getCreatedIn()) {
      balanceDtl.setCreatedIn(LocalDateTime.now());
    }
    return balanceDtlMapper.insertSelective(balanceDtl) == 1;
  }

}
