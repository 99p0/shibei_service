//package com.birdplanet.service.pay.config.alipay;
//
//import com.birdplanet.service.pay.config.IPayConfig;
//
///**
// * Created by dwy on 2017/5/18.
// */
//public interface IAlipayConfig extends IPayConfig {
//
//  /**
//   * @return 商户appid
//   */
//  String getAppId();
//
//  String getServerUrl();
//
//  /**
//   * 支付宝公钥
//   *
//   * @return 支付宝公钥
//   */
//  String getPublicKeyAlipay();
//
//  /**
//   * 私钥
//   *
//   * @return 私钥 pkcs8格式的
//   */
//  String getPrivateKey();
//
//  /**
//   * 公钥
//   *
//   * @return 公钥
//   */
//  String getPublicKey();
//
//  /**
//   * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//   *
//   * @return 地址
//   */
//  String getReturnUrl();
//
//  /**
//   * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//   *
//   * @return 地址
//   */
//  String getNotifyUrl();
//
//  /**
//   * 授权访问令牌的授权类型
//   */
//  String getGrantType();
//
//  String getRedirectUrl();
//
//  /**
//   * @return 编码
//   */
//  String getCharset();
//
//  /**
//   * @return 返回格式
//   */
//  String getFormat();
//
//  /**
//   * @return RSA2
//   */
//  String getSignType();
//
//  String getPayChannelDef();
//}
