/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AlipayService
 * @date 2019-07-08 09:56
 */
public interface IAlipayService {

  /**
   * 功能：支付宝手机网站支付接口(alipay.trade.wap.pay)
   *
   * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号，必填
   * @param subject      订单名称，必填
   * @param total_amount 付款金额，必填
   * @param body         商品描述，可空
   * @param appId        可空
   * @throws AlipayApiException
   * @throws UnsupportedEncodingException
   */
  String payWap(long uid, String out_trade_no, String subject, BigDecimal total_amount, String body,
      String appId);

  /**
   * @param uid
   * @param out_trade_no
   * @param subject
   * @param total_amount
   * @param body
   * @return
   */
  String payWap(long uid, String out_trade_no, String subject, BigDecimal total_amount,
      String body);

  /**
   * @param state
   * @param appId
   * @return
   */
  String oauthBase(String state, String appId);

  /**
   * @param state
   * @param appId
   * @return
   */
  String oauthUser(String state, String appId);

  /**
   * 获取用户信息
   */
  AlipayUserInfoShareResponse getUserInfo(String authToken);

  /**
   * @param appId
   * @param authToken
   * @return
   */
  AlipayUserInfoShareResponse getUserInfo(String appId, String authToken);

  /**
   * 根据auth_code 获取 access_token
   */
  AlipaySystemOauthTokenResponse getAccessToken(String appId, String scope, String authCode,
      String grantType);

  /**
   * @param outBizNo
   * @param useUserId
   * @param payeeAccount
   * @param amount
   * @param remark
   * @return
   * @throws AlipayApiException
   */
  AlipayFundTransToaccountTransferResponse transferByUid(String outBizNo, boolean useUserId,
      String payeeAccount, BigDecimal amount, String remark) throws AlipayApiException;

  /**
   * @param outBizNo
   * @param orderId
   * @return
   * @throws AlipayApiException
   */
  AlipayFundTransOrderQueryResponse transferQuery(String outBizNo, String orderId)
      throws AlipayApiException;

  AlipayTradeQueryResponse orderQuery(String outTradeNo, String tradeNo);

  /**
   * @param appId
   * @param targetId
   * @return
   */
  String getAppAuthInfo(String appId, String targetId);

  /**
   * @param uid
   * @param appId
   * @param ordersn
   * @param subject
   * @param amountDecimal
   * @param body
   * @return
   */
  String createAppOrder(long uid, String appId, String ordersn, String subject,
      BigDecimal amountDecimal, String body, String channel);
}
