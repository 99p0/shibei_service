/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IIdGenerateService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.daka.infrastructure.service.pay.IAlipayService;
import cn.birdplanet.daka.infrastructure.service.pay.IWxpayService;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchController
 * @date 2019-07-08 09:36
 */
@Api(tags = "交易 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/trade")
public class TradeController extends BaseController {

  @Autowired private IUserService userService;
  @Autowired private IWalletService walletService;
  @Autowired private IAlipayService alipayService;
  @Autowired private IWxpayService wxpayService;
  @Autowired private IIdGenerateService idGenerateService;

  /**
   * 金额参数是否在范围内
   *
   * @param amount 金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
   * @return -1 小于   0 等于 1 大于
   */
  private static boolean checkAmountRange(BigDecimal amount) {

    BigDecimal smallVal = new BigDecimal("0.01");
    BigDecimal bigVal = new BigDecimal("1000000.00");
    BigDecimal val = NumberUtil.format_s2(amount);
    // 取值范围[0.01,100000000]
    return val.compareTo(smallVal) != -1 && val.compareTo(bigVal) != 1;
  }

  @ApiOperation(value = "使用钱包支付", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "支付金额"),
  })
  @PostMapping("wallet")
  public RespDto payWithWallet(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "amount", defaultValue = "999999.99") String amount) {
    BigDecimal amountDecimal;
    try {
      amountDecimal = new BigDecimal(amount);
    } catch (Exception e) {
      log.debug("{} >>> 充值金额[{}]不是有效的数字", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.params_err);
    }
    amountDecimal = NumberUtil.format_s2(amountDecimal);
    if (!checkAmountRange(amountDecimal)) {
      log.debug("{} >>> 充值金额[{}]太大", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.recharge_amount_true);
    }
    return walletService.pay(currUserDtlVo.getUid(), amountDecimal);
    //Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    //dataMap.put("flag", payFlag);
    //dataMap.put("msg", payFlag ? "" : "钱包余额不足");
    //return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "使用支付宝支付", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "支付金额"),
  })
  @PostMapping("alipay")
  public RespDto payWithAlipay(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "channel", defaultValue = "h5") String channel,
      @RequestParam(name = "amount", defaultValue = "999999.99") String amount) {
    BigDecimal amountDecimal;
    try {
      amountDecimal = new BigDecimal(amount);
    } catch (Exception e) {
      log.debug("{} >>> 充值金额[{}]不是有效的数字", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.params_err);
    }
    amountDecimal = NumberUtil.format_s2(amountDecimal);
    if (!checkAmountRange(amountDecimal)) {
      log.debug("{} >>> 充值金额[{}]太大", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.recharge_amount_enough);
    }
    String ordersn = String.valueOf(idGenerateService.ordersn());
    String subject = "小鸟星球用户订单";
    String body = new StringBuilder()
        .append("小鸟星球用户")
        .append(currUserDtlVo.getUid())
        .append("使用")
        .append(TradeChannelCodes.alipay.getDesc())
        .append("于")
        .append(channel)
        .append("中购买星球币")
        .toString();

    String form =
        alipayService.payWap(currUserDtlVo.getUid(), ordersn, subject, amountDecimal, body);
    Map dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("form", form);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "生产支付宝支付的订单", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "需支付金额"),
      @ApiImplicitParam(paramType = "query", name = "appId", dataType = "int", value = "需支付金额"),
      @ApiImplicitParam(paramType = "query", name = "channel", dataType = "int", value = "付款渠道：1支付宝聚合，2支付宝-余额和银行卡，3花呗，4花呗分期，5信用卡"),
  })
  @PostMapping("alipay/create")
  public RespDto createAlipayOrder(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "") String appId,
      @RequestParam(required = false, defaultValue = "1") String channel,
      @RequestParam(name = "amount", defaultValue = "999999.99") String amount) {
    BigDecimal amountDecimal;
    try {
      amountDecimal = new BigDecimal(amount);
    } catch (Exception e) {
      log.debug("{} >>> 充值金额[{}]不是有效的数字", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.params_err);
    }
    amountDecimal = NumberUtil.format_s2(amountDecimal);
    if (!checkAmountRange(amountDecimal)) {
      log.debug("{} >>> 充值金额[{}]太大", currUserDtlVo, amount);
      return RespDto.error(ErrorCodes.recharge_amount_enough);
    }
    String ordersn = String.valueOf(idGenerateService.ordersn());
    String subject ="小鸟星球用户订单";
    String body = new StringBuilder()
        .append("小鸟星球用户")
        .append(currUserDtlVo.getUid())
        .append("使用")
        .append(TradeChannelCodes.alipay.getDesc())
        .append("于App中购买星球币")
        .toString();

    String form =
        alipayService.createAppOrder(currUserDtlVo.getUid(), appId, ordersn, subject, amountDecimal,
            body, channel);
    Map dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("form", form);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "查询支付结果", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "out_trade_no", dataType = "String", value = ""),
      @ApiImplicitParam(paramType = "query", name = "trade_no", dataType = "String", value = ""),
  })
  @PostMapping("alipay/order")
  public RespDto queryAlipayOrder(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "out_trade_no", defaultValue = "") String out_trade_no,
      @RequestParam(name = "trade_no", defaultValue = "") String trade_no) {

    //
    if (StringUtils.isBlank(out_trade_no) || StringUtils.isBlank(trade_no)) {
      return RespDto.error(ErrorCodes.params_err);
    }
    //

    alipayService.orderQuery(out_trade_no, trade_no);
    return RespDto.succMsg("");
  }

  @ApiOperation(value = "使用微信支付", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "支付金额"),
  })
  @PostMapping("wxpay")
  public RespDto payWithWxpay(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "amount", defaultValue = "999999.99") String amount) {

    return RespDto.succMsg("");
  }
}
