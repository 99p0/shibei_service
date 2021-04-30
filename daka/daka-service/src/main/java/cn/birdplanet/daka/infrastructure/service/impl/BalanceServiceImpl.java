/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.infrastructure.persistence.punch.BalanceDtlMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.IsReadCodes;
import com.github.pagehelper.PageHelper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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

  @Override
  public List<BalanceDtl> getByUidWithPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(BalanceDtl.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return balanceDtlMapper.selectByExample(example);
  }

  @Override public List<BalanceDtl> getAllWithPage(int pageNum, int pageSize) {
    return this.getByUidWithPage(pageNum, pageSize, 0L);
  }
}
