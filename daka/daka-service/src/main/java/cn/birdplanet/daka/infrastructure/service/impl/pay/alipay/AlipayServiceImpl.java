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
 * @author ??????[uncle.yang@outlook.com]
 * @title: AlipayServiceImpl
 * @date 2019-07-08 09:57
 */
@Slf4j
@Service
public class AlipayServiceImpl extends BaseService implements IAlipayService {

  @Autowired private IRechargeService rechargeService;
  @Autowired private IPayService payService;

  /**
   * ????????????????????????
   *
   * @return ???????????????
   */
  private AlipayClient getAlipayClient(String appId, AlipayTypeCodes alipayTypeCodes) {
    AlipayConfig alipay = payService.getAlipayConfig(appId, alipayTypeCodes);
    if (null == alipay) {
      throw new RuntimeException("????????????????????????");
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
    // ???????????? ??????
    String timeout_express = "2m";
    // ??????????????? ??????
    String product_code = "QUICK_WAP_WAY";
    /**********************/
    // SDK ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    //??????RSA????????????

    AlipayTradeWapPayRequest wapPayRequest = new AlipayTradeWapPayRequest();

    // ????????????????????????
    AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
    model.setOutTradeNo(out_trade_no);
    model.setSubject(subject);
    model.setTotalAmount(NumberUtil.format2Str(total_amount));
    model.setBody(body);
    model.setTimeoutExpress(timeout_express);
    model.setProductCode(product_code);
    model.setEnablePayChannels(alipay.getChannels());
    wapPayRequest.setBizModel(model);
    // ????????????????????????
    wapPayRequest.setNotifyUrl(alipay.getNotifyUrl());
    // ??????????????????
    wapPayRequest.setReturnUrl(alipay.getReturnUrl());

    // form????????????
    String form = "";
    try {
      // ??????SDK????????????
      form = this.getAlipayClient(alipay.getAppId()).pageExecute(wapPayRequest).getBody();
      // ??????????????????
      Recharge record =
          new Recharge(uid, TradeChannelCodes.alipay, out_trade_no, form, total_amount,
              TradeStatusCodes.WAIT_BUYER_PAY);
      record.setReturnUrl(alipay.getReturnUrl());
      record.setNotifyUrl(alipay.getNotifyUrl());
      record.setAppId(alipay.getAppId());
      rechargeService.initRecharge(record);
    } catch (AlipayApiException e) {
      log.error("???????????????SDK??????wap???form??????", e);
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
      //????????????
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
      // ????????????????????????H5
      if (StringUtils.isBlank(scope) || scope.equalsIgnoreCase("auth_base")) {
        typeCodes = AlipayTypeCodes.def_h5;
      } else {
        typeCodes = AlipayTypeCodes.codeOf(scope);
      }
    } catch (Exception e) {
      log.debug("??????????????????????????????scope::{} ,e{}", scope, e);
      typeCodes = AlipayTypeCodes.def_h5;
    }
    try {
      return this.getAlipayClient(appId, typeCodes).execute(request);
    } catch (AlipayApiException e) {
      //????????????
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
      log.error("?????????????????????????????????", e);
    }
    return url.toString();
  }

  /**
   * alipay.fund.trans.toaccount.transfer	???????????????????????????????????? * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
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
    log.debug("?????? >>> outBizNo:{}, payeeAccount:{}, amount:{},  response:{}", outBizNo,
        payeeAccount, amount, response);
    return response;
  }

  /**
   * alipay.trade.query(??????????????????????????????) ???????????????????????? https://opendocs.alipay.com/apis/api_1/alipay.trade.query
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
    // ?????????????????????appId ????????????
    try {
      AlipayTradeQueryResponse response = this.getAlipayClient().execute(request);
      log.debug("???????????? >>> outTradeNo:{}, tradeNo:{} response:{}", outTradeNo, tradeNo, response);
      if (response.isSuccess()) {
        System.out.println("????????????");
        // response.getBody()
      } else {
        System.out.println("????????????");
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
    // ?????????????????????appId ????????????
    try {
      AlipayUserCertdocCertverifyPreconsultResponse response =
          this.getAlipayClient().execute(request);
      if (response.isSuccess()) {
        return response.getVerifyId();
      }
    } catch (AlipayApiException e) {
      log.error("????????????ID?????? ::", e);
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
    // ?????????????????????appId ????????????
    try {
      AlipayUserCertdocCertverifyConsultResponse response = this.getAlipayClient().execute(request);
      if (response.isSuccess()) {
        dataMap.put("flag", response.getPassed().equalsIgnoreCase("T"));
        dataMap.put("fail_reason", response.getFailReason());
      }
    } catch (AlipayApiException e) {
      dataMap.put("flag", false);
      dataMap.put("fail_reason", "??????");
    }
    return dataMap;
  }

  /**
   * alipay.fund.trans.order.query	???????????????????????? https://docs.open.alipay.com/api_28/alipay.fund.trans.order.query
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
    log.debug("???????????? >>> outBizNo:{}, orderId:{} response:{}", outBizNo, orderId, response);
    return response;
  }

  @Override public String getAppAuthInfo(String appId, String targetId) {

    if (StringUtils.isBlank(targetId)) {
      targetId = String.valueOf(idGenerateService.ordersn());
    }
    //
    AlipayConfig alipay = payService.getAlipayConfig(appId, AlipayTypeCodes.app_oauth);
    if (null == alipay) {
      throw new RuntimeException("????????????????????????");
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
      throw new RuntimeException("????????????????????????");
    }
    boolean rsa2 = alipay.getSignType().equalsIgnoreCase("rsa2");
    // ??????????????????????????? https://opendocs.alipay.com/open/common/wifww7
    // balance,pcredit,pcreditpayInstallment,moneyFund,debitCardExpress,bankPay,creditCard,creditCardExpress
    if ("2".equalsIgnoreCase(channel)) {
      channel = "balance,moneyFund,debitCardExpress,bankPay";// ???????????????????????????
    } else if ("3".equalsIgnoreCase(channel)) {
      channel = "pcredit";// ??????
    } else if ("4".equalsIgnoreCase(channel)) {
      channel = "pcreditpayInstallment";// ????????????
    } else if ("5".equalsIgnoreCase(channel)) {
      channel = "creditCardExpress,creditCard";// ?????????
    } else {
      channel = alipay.getChannels();
    }
    Map<String, String> params =
        AlipayParamsBuildUtils.buildOrderParamMap(alipay.getAppId(), amountDecimal, subject, body,
            ordersn, rsa2, channel, alipay.getNotifyUrl());
    String orderParam = AlipayParamsBuildUtils.buildOrderParam(params);

    String sign = AlipayParamsBuildUtils.getSign(params, alipay.getPrivateKey(), rsa2);
    String orderInfo = orderParam + "&" + sign;

    // ??????????????????
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

