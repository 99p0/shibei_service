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
 * @author 杨润[uncle.yang@outlook.com]
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
    // 如果钱包的钱不能小于需要支付的金额
    if (user.getWallet().compareTo(amount) == -1) {
      return RespDto.error(ErrorCodes.wallet_not_enough);
    }
    //
    boolean walletFlag = userMapper.rechargeBalanceFromWallet(uid, amount) == 1;
    // 钱包- ， 余额+
    WalletDtl walletDtl = new WalletDtl(uid, "-" + NumberUtil.format3Str(amount), "充值到余额");
    this.addWalletDtl(walletDtl, WalletDtlTypeCodes._rechargeToBalance);
    BalanceDtl balanceDtl = new BalanceDtl(uid, "+" + amount.intValue(),
        TradeChannelCodes.wallet.getDesc() + "充值");
    balanceService.addBalanceDtl(balanceDtl, BalanceDtlTypeCodes._rechargeWithWallet);

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("flag", walletFlag);
    dataMap.put("msg", walletFlag ? "" : "钱包余额不足");
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
   * 每个人的限额，不一样
   *
   * @param uid 用户ID
   * @return
   */
  private BigDecimal getLimitAmountByUid(long uid) {
    // todo 目前统一金额
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
          // 防止 缓存穿透
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
      // 加入 redis
      redisUtils.set1Week(keys, limitList);
    }
    return limitList;
  }

  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public synchronized ActionVo withdraw(long uid, BigDecimal amount) throws BusinessException {
    User realUser = userService.getByUid(uid);
    // 每3天限提现一次
    String key = RedisConstants.TODAY_WITHDRAW_TIMES_KEY_PREFIX + uid;
    long expire = redisUtils.getExpire(key);
    if (expire <= 0) {
      redisUtils.del(key);
      log.debug("上次提现超过固定时间， 可以继续提现");
    }
    Integer times = (Integer) redisUtils.get(key);
    // 提现过 今日提现
    if ((null != times && times.intValue() >= 1)) {
      return ActionVo.error(ErrorCodes.wallet_withdraw_days_times);
    }
    // 金额是否足够
    if (realUser.getWallet().compareTo(amount) == -1) {
      return ActionVo.error(ErrorCodes.wallet_not_enough);
    }
    // 提现金额
    BigDecimal withdrawAmount = amount;
    // 提现手续费
    BigDecimal withdrawalFee = new BigDecimal("0.00");

    // 提现20元内无手续费，20元以上收1%手续费。 》 fix limit 120
    // 每个人的限额不一样：从60，120，250

    if (amount.compareTo(this.getLimitAmountByUid(uid)) != -1) {
      withdrawalFee = amount.multiply(BirdplanetConstants.WITHDRAWAL_FEE);
      withdrawAmount = amount.subtract(withdrawalFee);
    }

    // 现在使用申请制度。。。
    userMapper.updateWalletSubtract(uid, amount);
    WalletWithdrawApp withdrawApp =
        new WalletWithdrawApp(uid, amount, withdrawalFee, withdrawAmount);
    walletWithdrawAppMapper.insertSelective(withdrawApp);
    // 申请提现记录
    WalletDtl walletDtl = new WalletDtl(uid, "-" + NumberUtil.format3Str(amount), "申请提现");
    this.addWalletDtl(walletDtl, WalletDtlTypeCodes._withdrawToAlipay);

    //String outBizNo = String.valueOf(idGenerateService.ordersn());
    //try {
    //  AlipayFundTransToaccountTransferResponse transferResp =
    //      alipayService.transferByUid(outBizNo, true, realUser.getOpenUid(), withdrawAmount,
    //          "小鸟星提现");
    //  if (transferResp.isSuccess()) {
    //    // 转账成功 更改钱包金额， 添加一条提现数据
    //    userMapper.updateWalletSubtract(realUser.getUid(), amount);
    //    transferMapper.insertSelective(
    //        new Transfer(uid, outBizNo, amount, withdrawAmount, withdrawalFee,
    //            transferResp.getOrderId(),
    //            JacksonUtil.obj2Json(transferResp), AlipayAccountTypeCodes.USERID,
    //            realUser.getOpenUid()));
    //  } else {
    //    log.debug("转账失败 >>> outBizNo:{}", outBizNo);
    //    AlipayFundTransOrderQueryResponse queryTransferResp =
    //        alipayService.transferQuery(transferResp.getOutBizNo(), transferResp.getOrderId());
    //    if (queryTransferResp.isSuccess()) {
    //      if (TransferStatusCodes.SUCCESS.getCode()
    //          .equalsIgnoreCase(queryTransferResp.getStatus())) {
    //        // 转账成功 更改钱包金额， 添加一条提现数据
    //        userMapper.updateWalletSubtract(realUser.getUid(), amount);
    //        transferMapper.insertSelective(
    //            new Transfer(uid, outBizNo, amount, withdrawAmount, withdrawalFee,
    //                transferResp.getOrderId(),
    //                JacksonUtil.obj2Json(transferResp), AlipayAccountTypeCodes.USERID,
    //                realUser.getOpenUid()));
    //      } else {
    //        log.debug("转账失败 >>> outBizNo:{}", queryTransferResp);
    //      }
    //    } else {
    //      log.debug("转账失败 >>> outBizNo:{}", queryTransferResp);
    //    }
    //  }
    //} catch (AlipayApiException e) {
    //  throw new PunchException(ErrorCodes.alipay_sdk_err);
    //}

    // 第一次提现奖励1元到余额
    if (realUser.getIsFirstWithdraw().equalsIgnoreCase(YesOrNoCodes.YES.getCode())) {
      //
      if (userMapper.updateFirstWithdraw(uid) == 1) {
        balanceService.addBalanceDtl(new BalanceDtl(uid, "+1", "首次提现奖励1元"),
            BalanceDtlTypeCodes._firstWithdraw);
      }
      // 用户首次提现 奖励邀请人2元
      User inviter = userMapper.getByInvitationCode(realUser.getInviterCode());
      if (null != inviter) {
        if (userMapper.updateBalanceAdd(inviter.getUid(), new BigDecimal("2")) == 1) {
          balanceService.addBalanceDtl(
              new BalanceDtl(inviter.getUid(), "+2", "好友“" + realUser.getNickName() + "”提现奖励"),
              BalanceDtlTypeCodes._firstWithdraw);
        }
      }
    }

    // 记录提现的日期， 有效期为： 当前时间到第3天0时的分钟数,间隔三天
    LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(0, 0, 0));
    Duration duration = Duration.between(LocalDateTime.now(), tomorrow);
    times = (null == times) ? 1 : times + 1;
    redisUtils.set(key, times, duration.toMinutes(), TimeUnit.MINUTES);
    // 记录下 最后一次提现时间

    return ActionVo.succMsg("已申请提现，请注意查收");
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
