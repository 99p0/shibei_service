/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.WalletWithdrawApp;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IWithdrawService
 * @date 2019-08-14 18:09
 */
public interface IWithdrawService {

  List<WalletWithdrawApp> getWithdrawApplist(int pageNum, int pageSize, Integer status);

  boolean confirmTransfer(long id, int status, String remark);
}
