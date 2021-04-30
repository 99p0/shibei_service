/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.WalletWithdrawApp;
import cn.birdplanet.daka.infrastructure.persistence.punch.WalletWithdrawAppMapper;
import cn.birdplanet.daka.infrastructure.service.IWithdrawService;
import cn.birdplanet.toolkit.date.DateUtil;
import com.github.pagehelper.PageHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: WithdrawServiceImpl
 * @date 2019-08-14 18:10
 */
@Slf4j
@Service
public class WithdrawServiceImpl extends BaseService implements IWithdrawService {

  @Autowired private WalletWithdrawAppMapper withdrawAppMapper;

  @Override
  public List<WalletWithdrawApp> getWithdrawApplist(int pageNum, int pageSize, Integer status) {
    Example example = new Example(WalletWithdrawApp.class);
    Example.Criteria criteria = example.createCriteria();
    if (null != status) {
      criteria.andEqualTo("status", status);
    }
    example.orderBy("status").asc().orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return withdrawAppMapper.selectByExample(example);
  }

  @Override public boolean confirmTransfer(long id, int status, String remark) {
    if (StringUtils.isBlank(remark)) {
      remark = "于" + DateUtil._sdf() + "手动转账";
    }
    return withdrawAppMapper.confirmTransfer(id, status, remark) == 1;
  }
}
