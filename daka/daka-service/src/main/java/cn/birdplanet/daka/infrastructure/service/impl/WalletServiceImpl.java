/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.po.WalletWithdrawApp;
import cn.birdplanet.daka.domain.po.WalletWithdrawalLimit;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.WalletDtlMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.WalletWithdrawAppMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.WalletWithdrawalLimitMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import cn.birdplanet.toolkit.extra.code.IsReadCodes;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import cn.birdplanet.toolkit.extra.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author ??????[uncle.yang@outlook.com]
 * @title: WalletServiceImpl
 * @date 2019-07-18 14:31
 */
@Slf4j
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class WalletServiceImpl extends BaseService implements IWalletService {

  @Autowired private IUserService userService;
  @Autowired private UserMapper userMapper;
  @Autowired private WalletDtlMapper walletDtlMapper;
  @Autowired private IBalanceService balanceService;
  @Autowired private WalletWithdrawalLimitMapper walletWithdrawalLimitMapper;
  @Autowired private WalletWithdrawAppMapper walletWithdrawAppMapper;

  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public synchronized RespDto pay(final long uid, final BigDecimal amount) {
    User user = userService.getByUid(uid);
    // ???????????????????????????????????????????????????
    if (user.getWallet().compareTo(amount) == -1) {
      return RespDto.error(ErrorCodes.wallet_not_enough);
    }
    //
    boolean walletFlag = userMapper.rechargeBalanceFromWallet(uid, amount) == 1;
    // ??????- ??? ??????+
    WalletDtl walletDtl = new WalletDtl(uid, "-" + NumberUtil.format3Str(amount), "???????????????");
    this.addWalletDtl(walletDtl, WalletDtlTypeCodes._rechargeToBalance);
    BalanceDtl balanceDtl = new BalanceDtl(uid, "+" + amount.intValue(),
        TradeChannelCodes.wallet.getDesc() + "??????");
    balanceService.addBalanceDtl(balanceDtl, BalanceDtlTypeCodes._rechargeWithWallet);

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("flag", walletFlag);
    dataMap.put("msg", walletFlag ? "" : "??????????????????");
    return RespDto.succData(dataMap);
  }

  @Override public boolean addWalletDtl(WalletDtl walletDtl, WalletDtlTypeCodes typeCode) {
    walletDtl.setType(typeCode.getCode());
    walletDtl.setIsRead(IsReadCodes.no.getCode());
    if (null == walletDtl.getCreatedIn()) {
      walletDtl.setCreatedIn(LocalDateTime.now());
    }
    return walletDtlMapper.insertSelective(walletDtl) == 1;
  }

  /**
   * ??????????????????????????????
   *
   * @param uid ??????ID
   * @return
   */
  private BigDecimal getLimitAmountByUid(long uid) {
    // todo ??????????????????
    return this.getCommonWithdrawalLimit().getLimitAmount();
  }

  private WalletWithdrawalLimit getLimitAmountByType(final GameModeActivityTypeCodes codes) {
    List<WalletWithdrawalLimit> limitList = this.getWithdrawalLimitList();
    WalletWithdrawalLimit limitAmount;
    limitAmount = limitList.stream()
        .filter(m -> m.getType().equalsIgnoreCase(codes.getCode()))
        .findFirst()
        .orElse(null);
    return limitAmount;
  }

  private WalletWithdrawalLimit getCommonWithdrawalLimit() {
    final String keys = RedisConstants.WALLET_WITHDRAWAL_LIMIT_COMMONS_KEY;
    WalletWithdrawalLimit limit = (WalletWithdrawalLimit) redisUtils.get(keys);
    if (null == limit) {
      synchronized (this) {
        limit = this.getWalletWithdrawalLimit();
        if (null != limit) {
          redisUtils.setDays(RedisConstants.WALLET_WITHDRAWAL_LIMIT_COMMONS_KEY, limit, 365);
        } else {
          limit = new WalletWithdrawalLimit(BirdplanetConstants.WITHDRAWAL_LIMIT);
          // ?????? ????????????
          redisUtils.setMinutes(keys, limit, 5);
        }
      }
    }
    return limit;
  }

  private WalletWithdrawalLimit getWalletWithdrawalLimit() {
    Example example = new Example(WalletWithdrawalLimit.class);
    example.createCriteria().andEqualTo("type", GameModeActivityTypeCodes.E.getCode());
    WalletWithdrawalLimit limit = walletWithdrawalLimitMapper.selectOneByExample(example);
    return limit;
  }

  private synchronized List<WalletWithdrawalLimit> getWithdrawalLimitList() {
    String keys = RedisConstants.WALLET_WITHDRAWAL_LIMIT_KEY;
    List<WalletWithdrawalLimit> limitList = (List<WalletWithdrawalLimit>) redisUtils.get(keys);
    if (null == limitList || limitList.isEmpty()) {
      limitList = walletWithdrawalLimitMapper.selectAll();
      // ?????? redis
      redisUtils.set1Week(keys, limitList);
    }
    return limitList;
  }

  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public synchronized ActionVo withdraw(long uid, BigDecimal amount) throws BusinessException {
    User realUser = userService.getByUid(uid);
    // ???3??????????????????
    String key = RedisConstants.TODAY_WITHDRAW_TIMES_KEY_PREFIX + uid;
    long expire = redisUtils.getExpire(key);
    if (expire <= 0) {
      redisUtils.del(key);
      log.debug("????????????????????????????????? ??????????????????");
    }
    Integer times = (Integer) redisUtils.get(key);
    // ????????? ????????????
    if ((null != times && times.intValue() >= 1)) {
      return ActionVo.error(ErrorCodes.wallet_withdraw_days_times);
    }
    // ??????????????????
    if (realUser.getWallet().compareTo(amount) == -1) {
      return ActionVo.error(ErrorCodes.wallet_not_enough);
    }
    // ????????????
    BigDecimal withdrawAmount = amount;
    // ???????????????
    BigDecimal withdrawalFee = new BigDecimal("0.00");

    // ??????20?????????????????????20????????????1%???????????? ??? fix limit 120
    // ?????????????????????????????????60???120???250

    if (amount.compareTo(this.getLimitAmountByUid(uid)) != -1) {
      withdrawalFee = amount.multiply(BirdplanetConstants.WITHDRAWAL_FEE);
      withdrawAmount = amount.subtract(withdrawalFee);
    }

    // ?????????????????????????????????
    userMapper.updateWalletSubtract(uid, amount);
    WalletWithdrawApp withdrawApp =
        new WalletWithdrawApp(uid, amount, withdrawalFee, withdrawAmount);
    walletWithdrawAppMapper.insertSelective(withdrawApp);
    // ??????????????????
    WalletDtl walletDtl = new WalletDtl(uid, "-" + NumberUtil.format3Str(amount), "????????????");
    this.addWalletDtl(walletDtl, WalletDtlTypeCodes._withdrawToAlipay);

    //String outBizNo = String.valueOf(idGenerateService.ordersn());
    //try {
    //  AlipayFundTransToaccountTransferResponse transferResp =
    //      alipayService.transferByUid(outBizNo, true, realUser.getOpenUid(), withdrawAmount,
    //          "???????????????");
    //  if (transferResp.isSuccess()) {
    //    // ???????????? ????????????????????? ????????????????????????
    //    userMapper.updateWalletSubtract(realUser.getUid(), amount);
    //    transferMapper.insertSelective(
    //        new Transfer(uid, outBizNo, amount, withdrawAmount, withdrawalFee,
    //            transferResp.getOrderId(),
    //            JacksonUtil.obj2Json(transferResp), AlipayAccountTypeCodes.USERID,
    //            realUser.getOpenUid()));
    //  } else {
    //    log.debug("???????????? >>> outBizNo:{}", outBizNo);
    //    AlipayFundTransOrderQueryResponse queryTransferResp =
    //        alipayService.transferQuery(transferResp.getOutBizNo(), transferResp.getOrderId());
    //    if (queryTransferResp.isSuccess()) {
    //      if (TransferStatusCodes.SUCCESS.getCode()
    //          .equalsIgnoreCase(queryTransferResp.getStatus())) {
    //        // ???????????? ????????????????????? ????????????????????????
    //        userMapper.updateWalletSubtract(realUser.getUid(), amount);
    //        transferMapper.insertSelective(
    //            new Transfer(uid, outBizNo, amount, withdrawAmount, withdrawalFee,
    //                transferResp.getOrderId(),
    //                JacksonUtil.obj2Json(transferResp), AlipayAccountTypeCodes.USERID,
    //                realUser.getOpenUid()));
    //      } else {
    //        log.debug("???????????? >>> outBizNo:{}", queryTransferResp);
    //      }
    //    } else {
    //      log.debug("???????????? >>> outBizNo:{}", queryTransferResp);
    //    }
    //  }
    //} catch (AlipayApiException e) {
    //  throw new PunchException(ErrorCodes.alipay_sdk_err);
    //}

    // ?????????????????????1????????????
    if (realUser.getIsFirstWithdraw().equalsIgnoreCase(YesOrNoCodes.YES.getCode())) {
      //
      if (userMapper.updateFirstWithdraw(uid) == 1) {
        balanceService.addBalanceDtl(new BalanceDtl(uid, "+1", "??????????????????1???"),
            BalanceDtlTypeCodes._firstWithdraw);
      }
      // ?????????????????? ???????????????2???
      User inviter = userMapper.getByInvitationCode(realUser.getInviterCode());
      if (null != inviter) {
        if (userMapper.updateBalanceAdd(inviter.getUid(), new BigDecimal("2")) == 1) {
          balanceService.addBalanceDtl(
              new BalanceDtl(inviter.getUid(), "+2", "?????????" + realUser.getNickName() + "???????????????"),
              BalanceDtlTypeCodes._firstWithdraw);
        }
      }
    }

    // ???????????????????????? ??????????????? ??????????????????3???0???????????????,????????????
    LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(0, 0, 0));
    Duration duration = Duration.between(LocalDateTime.now(), tomorrow);
    times = (null == times) ? 1 : times + 1;
    redisUtils.set(key, times, duration.toMinutes(), TimeUnit.MINUTES);
    // ????????? ????????????????????????

    return ActionVo.succMsg("?????????????????????????????????");
  }

  @Override public List<WalletDtl> getWalletDtlByPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(WalletDtl.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return walletDtlMapper.selectByExample(example);
  }

  @Override public List<WalletDtl> getAllByPage(int pageNum, int pageSize) {

    return this.getWalletDtlByPage(pageNum, pageSize, 0L);
  }
}
