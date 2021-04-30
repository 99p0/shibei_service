package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.AlipayConfig;
import cn.birdplanet.toolkit.extra.code.AlipayTypeCodes;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IPaymentChannelService
 * @date 2019/12/5 19:49
 */
public interface IPayService {

  /**
   * 获取支付宝支付的具体支付渠道
   *
   * @return vo
   */
  AlipayConfig getAlipayConfig(String appId);

  AlipayConfig getAlipayConfig(String appId, AlipayTypeCodes codes);
}
