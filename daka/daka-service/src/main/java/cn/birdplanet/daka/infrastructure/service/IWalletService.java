/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: WalletService
 * @date 2019-07-18 14:31
 */
public interface IWalletService {

  /**
   * 使用钱包支付， 添加日志
   *
   * @param user_id 用户的ID
   * @param amount 金额
   * @return 支付结果
   */
  RespDto pay(long user_id, BigDecimal amount);

  /**
   * _ 提现说明 · 最低提现金额10元。
   * <p>
   * · 提现金额不能有小数点。
   * <p>
   * · 提现20元内无手续费，20元以上收1%手续费。
   * <p>
   * · 每个用户每天限提现一次。
   * <p>
   * · 第一次提现奖励1元到您的余额
   *
   * @param user_id 提现用户
   * @param amount 提现金额
   * @return 提现结果
   */
  ActionVo withdraw(long user_id, BigDecimal amount) throws BusinessException;

  /**
   *
   * @param walletDtl
   * @param typeCode
   * @return
   */
  boolean addWalletDtl(WalletDtl walletDtl, WalletDtlTypeCodes typeCode);

  /**
   *
   * @param pageNum
   * @param pageSize
   * @param uid
   * @return
   */
  List<WalletDtl> getWalletDtlByPage(int pageNum, int pageSize, long uid);

  /**
   *
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<WalletDtl> getAllByPage(int pageNum, int pageSize);
}
