/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl.pay.alipay;

import cn.birdplanet.daka.domain.po.AlipayConfig;
import cn.birdplanet.daka.domain.po.Recharge;
import cn.birdplanet.daka.infrastructure.service.IPayService;
import cn.birdplanet.daka.infrastructure.service.IRechargeService;
import cn.birdplanet.daka.infrastructure.service.impl.BaseService;
import cn.birdplanet.daka.infrastructure.service.pay.IAlipayService;
import cn.birdplanet.toolkit.AlipayParamsBuildUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.AlipayAccountTypeCodes;
import cn.birdplanet.toolkit.extra.code.AlipayTypeCodes;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.TradeStatusCodes;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.request.AlipayUserCertdocCertverifyConsultRequest;
import com.alipay.api.request.AlipayUserCertdocCertverifyPreconsultRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayUserCertdocCertverifyConsultResponse;
import com.alipay.api.response.AlipayUserCertdocCertverifyPreconsultResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.google.common.collect.Maps;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AlipayServiceImpl
 * @date 2019-07-08 09:57
 */
@Slf4j
@Service
public class AlipayServiceImpl extends BaseService implements IAlipayService {

  @Autowired private IRechargeService rechargeService;
  @Autowired private IPayService payService;

  /**
   * 初始化支付宝配置
   *
   * @return 支付宝实例
   */
  private AlipayClient getAlipayClient(String appId, AlipayTypeCodes alipayTypeCodes) {
    AlipayConfig alipay = payService.getAlipayConfig(appId, alipayTypeCodes);
    if (null == alipay) {
      throw new RuntimeException("获取支付方式失败");
    }
    //
    AlipayClient client = new DefaultAlipayClient(alipay.getServerUrl(), alipay.getAppId(),
        alipay.getPrivateKey(),
        alipay.getFormat(), alipay.getCharset(), alipay.getPublicKeyAli(),
        alipay.getSignType());
    return client;
  }

  private AlipayClient getAlipayClient() {
    return this.getAlipayClient(null, AlipayTypeCodes.def_h5);
  }

  private AlipayClient getAlipayClient(AlipayTypeCodes alipayTypeCodes) {
    return this.getAlipayClient(null, alipayTypeCodes);
  }

  private AlipayClient getAlipayClient(String appId) {
    return this.getAlipayClient(appId, AlipayTypeCodes.def_h5);
  }

  @Override
  public String payWap(long uid, String out_trade_no, String subject, BigDecimal total_amount,
      String body) {
    return this.payWap(uid, out_trade_no, subject, total_amount, body, null);
  }

  @Override
  public String payWap(long uid, String out_trade_no, String subject, BigDecimal total_amount,
      String body, String appId) {
    AlipayConfig alipay = payService.getAlipayConfig(appId);
    // 超时时间 可空
    String timeout_express = "2m";
    // 销售产品码 必填
    String product_code = "QUICK_WAP_WAY";
    /**********************/
    // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
    //调用RSA签名方式

    AlipayTradeWapPayRequest wapPayRequest = new AlipayTradeWapPayRequest();

    // 封装请求支付信息
    AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
    model.setOutTradeNo(out_trade_no);
    model.setSubject(subject);
    model.setTotalAmount(NumberUtil.format2Str(total_amount));
    model.setBody(body);
    model.setTimeoutExpress(timeout_express);
    model.setProductCode(product_code);
    model.setEnablePayChannels(alipay.getChannels());
    wapPayRequest.setBizModel(model);
    // 设置异步通知地址
    wapPayRequest.setNotifyUrl(alipay.getNotifyUrl());
    // 设置同步地址
    wapPayRequest.setReturnUrl(alipay.getReturnUrl());

    // form表单生产
    String form = "";
    try {
      // 调用SDK生成表单
      form = this.getAlipayClient(alipay.getAppId()).pageExecute(wapPayRequest).getBody();
      // 保存交易记录
      Recharge record =
          new Recharge(uid, TradeChannelCodes.alipay, out_trade_no, form, total_amount,
              TradeStatusCodes.WAIT_BUYER_PAY);
      record.setReturnUrl(alipay.getReturnUrl());
      record.setNotifyUrl(alipay.getNotifyUrl());
      record.setAppId(alipay.getAppId());
      rechargeService.initRecharge(record);
    } catch (AlipayApiException e) {
      log.error("调用支付宝SDK生成wap的form出错", e);
    }
    return form;
  }

  @Override
  public AlipayUserInfoShareResponse getUserInfo(String authToken) {
    return this.getUserInfo(null, authToken);
  }

  @Override
  public AlipayUserInfoShareResponse getUserInfo(String appId, String authToken) {
    AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
    try {
      AlipayUserInfoShareResponse response =
          this.getAlipayClient(appId).execute(request, authToken);
      return response;
    } catch (AlipayApiException e) {
      //处理异常
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public AlipaySystemOauthTokenResponse getAccessToken(String appId, String scope, String authCode,
      String grantType) {
    AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
    request.setCode(authCode);
    request.setGrantType(grantType);
    AlipayTypeCodes typeCodes;
    try {
      // 静默授权，也属于H5
      if (StringUtils.isBlank(scope) || scope.equalsIgnoreCase("auth_base")) {
        typeCodes = AlipayTypeCodes.def_h5;
      } else {
        typeCodes = AlipayTypeCodes.codeOf(scope);
      }
    } catch (Exception e) {
      log.debug("不存在此类型的支付宝scope::{} ,e{}", scope, e);
      typeCodes = AlipayTypeCodes.def_h5;
    }
    try {
      return this.getAlipayClient(appId, typeCodes).execute(request);
    } catch (AlipayApiException e) {
      //处理异常
      log.error("getAccessToken error", e);
    }
    return null;
  }

  @Override
  public String oauthBase(String state, String appId) {
    return this.auth(state, appId, "auth_base");
  }

  @Override
  public String oauthUser(String state, String appId) {
    return this.auth(state, appId, "auth_user");
  }

  private String auth(String state, String appId, String scope) {
    StringBuilder url;
    AlipayConfig alipay = payService.getAlipayConfig(appId);
    url = new StringBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=")
        .append(alipay.getAppId())
        .append("&scope=").append(scope)
        .append("&state=").append(state)
        .append("&redirect_uri=");
    try {
      url.append(URLEncoder.encode(alipay.getRedirectUrl(), alipay.getCharset()));
    } catch (UnsupportedEncodingException e) {
      log.error("组装支付宝授权链接异常", e);
    }
    return url.toString();
  }

  /**
   * alipay.fund.trans.toaccount.transfer	单笔转账到支付宝账户接口 * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
   *
   * @throws AlipayApiException
   */
  @Override
  public AlipayFundTransToaccountTransferResponse transferByUid(String outBizNo,
      boolean useUserId, String payeeAccount,
      BigDecimal amount, String remark) throws AlipayApiException {
    AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
    request.setBizContent("{" +
        "    \"out_biz_no\":\"" + outBizNo + "\"," +
        "    \"payee_type\":\"" + (useUserId ? AlipayAccountTypeCodes.USERID.getCode()
        : AlipayAccountTypeCodes.LOGONID.getCode()) + "\"," +
        "    \"payee_account\":\"" + payeeAccount + "\"," +
        "    \"amount\":\"" + NumberUtil.format2Str(amount) + "\"," +
        //                "    \"payer_show_name\":\"" + payer_show_name + "\"," +
        //                "    \"payee_real_name\":\"" + payee_real_name + "\"," +
        "    \"remark\":\"" + remark + "\"," +
        "  }");
    AlipayFundTransToaccountTransferResponse response =
        this.getAlipayClient().execute(request);
    log.debug("转账 >>> outBizNo:{}, payeeAccount:{}, amount:{},  response:{}", outBizNo,
        payeeAccount, amount, response);
    return response;
  }

  /**
   * alipay.trade.query(统一收单线下交易查询) 查询转账订单接口 https://opendocs.alipay.com/apis/api_1/alipay.trade.query
   *
   * @throws AlipayApiException
   */
  @Override
  public AlipayTradeQueryResponse orderQuery(String outTradeNo, String tradeNo) {
    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
    request.setBizContent("{" +
        "    \"out_trade_no\":\"" + outTradeNo + "\"," +
        "    \"trade_no\":\"" + tradeNo + "\"" +
        "  }");
    // 查询数据，根据appId 查询数据
    try {
      AlipayTradeQueryResponse response = this.getAlipayClient().execute(request);
      log.debug("交易查询 >>> outTradeNo:{}, tradeNo:{} response:{}", outTradeNo, tradeNo, response);
      if (response.isSuccess()) {
        System.out.println("调用成功");
        // response.getBody()
      } else {
        System.out.println("调用失败");
      }
    } catch (AlipayApiException e) {
      e.printStackTrace();
    }

    return null;
  }

  public String getCertVerifyId(String userName, String certNo) {
    AlipayUserCertdocCertverifyPreconsultRequest request =
        new AlipayUserCertdocCertverifyPreconsultRequest();
    request.setBizContent("{" +
        "\"user_name\":\"" + userName + "\"," +
        "\"cert_type\":\"IDENTITY_CARD\"," +
        "\"cert_no\":\"" + certNo + "\"," +
        //"\"mobile\":\"13812345678\"," +
        //"\"logon_id\":\"sample@sample.com\"," +
        "\"ext_info\":\"{}\"" +
        "  }");
    // 查询数据，根据appId 查询数据
    try {
      AlipayUserCertdocCertverifyPreconsultResponse response =
          this.getAlipayClient().execute(request);
      if (response.isSuccess()) {
        return response.getVerifyId();
      }
    } catch (AlipayApiException e) {
      log.error("获取认证ID异常 ::", e);
    }
    return "";
  }

  public Map getCertVerifyResult(String verifyId) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    AlipayUserCertdocCertverifyConsultRequest request =
        new AlipayUserCertdocCertverifyConsultRequest();
    request.setBizContent("{" +
        "\"verify_id\":\"" + verifyId + "\"" +
        "  }");
    // 查询数据，根据appId 查询数据
    try {
      AlipayUserCertdocCertverifyConsultResponse response = this.getAlipayClient().execute(request);
      if (response.isSuccess()) {
        dataMap.put("flag", response.getPassed().equalsIgnoreCase("T"));
        dataMap.put("fail_reason", response.getFailReason());
      }
    } catch (AlipayApiException e) {
      dataMap.put("flag", false);
      dataMap.put("fail_reason", "异常");
    }
    return dataMap;
  }

  /**
   * alipay.fund.trans.order.query	查询转账订单接口 https://docs.open.alipay.com/api_28/alipay.fund.trans.order.query
   *
   * @throws AlipayApiException
   */
  @Override
  public AlipayFundTransOrderQueryResponse transferQuery(String outBizNo, String orderId)
      throws AlipayApiException {
    AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
    request.setBizContent("{" +
        "    \"out_biz_no\":\"" + outBizNo + "\"," +
        "    \"order_id\":\"" + orderId + "\"" +
        "  }");
    AlipayFundTransOrderQueryResponse response = this.getAlipayClient().execute(request);
    log.debug("查询转账 >>> outBizNo:{}, orderId:{} response:{}", outBizNo, orderId, response);
    return response;
  }

  @Override public String getAppAuthInfo(String appId, String targetId) {

    if (StringUtils.isBlank(targetId)) {
      targetId = String.valueOf(idGenerateService.ordersn());
    }
    //
    AlipayConfig alipay = payService.getAlipayConfig(appId, AlipayTypeCodes.app_oauth);
    if (null == alipay) {
      throw new RuntimeException("获取支付方式失败");
    }
    boolean rsa2 = alipay.getSignType().equalsIgnoreCase("rsa2");
    //
    Map<String, String> authInfoMap =
        AlipayParamsBuildUtils.buildAppAuthInfoMap(alipay.getPid(), alipay.getAppId(), targetId,
            rsa2);
    //
    String info = AlipayParamsBuildUtils.buildOrderParam(authInfoMap);
    //
    String sign = AlipayParamsBuildUtils.getSign(authInfoMap, alipay.getPrivateKey(), rsa2);
    //
    String authInfo = info + "&" + sign;
    log.debug("getAppAuthInfo >>{}", authInfo);
    return authInfo;
  }

  @Override
  public String createAppOrder(long uid, String appId, String ordersn, String subject,
      BigDecimal amountDecimal, String body, String channel) {

    AlipayConfig alipay = payService.getAlipayConfig(appId, AlipayTypeCodes.app_pay);
    if (null == alipay) {
      throw new RuntimeException("获取支付方式失败");
    }
    boolean rsa2 = alipay.getSignType().equalsIgnoreCase("rsa2");
    // 支付渠道列表详见： https://opendocs.alipay.com/open/common/wifww7
    // balance,pcredit,pcreditpayInstallment,moneyFund,debitCardExpress,bankPay,creditCard,creditCardExpress
    if ("2".equalsIgnoreCase(channel)) {
      channel = "balance,moneyFund,debitCardExpress,bankPay";// 银行卡和支付宝余额
    } else if ("3".equalsIgnoreCase(channel)) {
      channel = "pcredit";// 花呗
    } else if ("4".equalsIgnoreCase(channel)) {
      channel = "pcreditpayInstallment";// 花呗分期
    } else if ("5".equalsIgnoreCase(channel)) {
      channel = "creditCardExpress,creditCard";// 信用卡
    } else {
      channel = alipay.getChannels();
    }
    Map<String, String> params =
        AlipayParamsBuildUtils.buildOrderParamMap(alipay.getAppId(), amountDecimal, subject, body,
            ordersn, rsa2, channel, alipay.getNotifyUrl());
    String orderParam = AlipayParamsBuildUtils.buildOrderParam(params);

    String sign = AlipayParamsBuildUtils.getSign(params, alipay.getPrivateKey(), rsa2);
    String orderInfo = orderParam + "&" + sign;

    // 保存交易记录
    Recharge record =
        new Recharge(uid, TradeChannelCodes.alipay, ordersn, orderInfo, amountDecimal,
            TradeStatusCodes.WAIT_BUYER_PAY);
    //record.setReturnUrl(alipay.getReturnUrl());
    record.setNotifyUrl(alipay.getNotifyUrl());
    record.setAppId(alipay.getAppId());
    rechargeService.initRecharge(record);
    return orderInfo;
  }
}

