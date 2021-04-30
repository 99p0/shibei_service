/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import cn.birdplanet.toolkit.extra.exception.BusinessException;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: WalletController
 * @date 2019-07-08 09:36
 */
@Api(tags = "钱包 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/wallet")
public class WalletController extends BaseController {

  @Autowired private IUserService userService;
  @Autowired private IWalletService walletService;

  @ApiOperation(value = "获取钱包金额", notes = "Notes")
  @PostMapping("amount")
  public RespDto getWalletAmount(@RequestAttribute UserDtlVO currUserDtlVo) {
    User trueUser = userService.getByUidFromRedis(currUserDtlVo.getUid());
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(1);
    dataMap.put("wallet", trueUser.getWallet());
    return RespDto.succData(dataMap);
  }

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
   * @param currUserDtlVo 当前用户
   * @param amount 提现金额
   * @return 请求结果
   */
  @ApiOperation(value = "提现", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "转账金额"),
  })
  @PostMapping("withdraw")
  public RespDto withdraw(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "amount") int amount) throws BusinessException {
    // 是否提现过，每3天限提现一次
    String key = RedisConstants.TODAY_WITHDRAW_TIMES_KEY_PREFIX + currUserDtlVo.getUid();
    long expire = redisUtils.getExpire(key);
    if (expire <= 0) {
      redisUtils.del(key);
    }
    Integer times = (Integer) redisUtils.get(key);
    // 提现过 今日提现
    if ((null != times && times.intValue() >= 1)) {
      return RespDto.error(ErrorCodes.wallet_withdraw_days_times);
    }

    // 最低提现金额10元
    int minimumAmount = 10,maxAmount = 6000;
    if (amount < minimumAmount) {
      return RespDto.error(ErrorCodes.wallet_withdraw_minimum_amount);
    }
    if (amount >= maxAmount) {
      return RespDto.error(ErrorCodes.wallet_withdraw_max_amount);
    }
    ActionVo actionVo = walletService.withdraw(currUserDtlVo.getUid(), new BigDecimal(amount));
    return RespDto.build(actionVo);
  }

  @ApiOperation(value = "钱包明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl")
  public RespDto getAllByPageAboutWallet(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<WalletDtl> list =
        walletService.getWalletDtlByPage(pageNum, pageSize, currUserDtlVo.getUid());
    PageInfo pageInfo = new PageInfo(list);
    pageInfo.setList(list);
    return RespDto.succData(pageInfo);
  }
}
