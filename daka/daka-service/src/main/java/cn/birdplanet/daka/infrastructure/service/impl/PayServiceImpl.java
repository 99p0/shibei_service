package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.AlipayConfig;
import cn.birdplanet.daka.infrastructure.persistence.punch.AlipayConfigMapper;
import cn.birdplanet.daka.infrastructure.service.IPayService;
import cn.birdplanet.toolkit.extra.code.AlipayTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PaymentChannelServiceImpl
 * @date 2019/12/5 19:49
 */
@Slf4j
@Service
public class PayServiceImpl extends BaseService implements IPayService {

  @Autowired private AlipayConfigMapper alipayConfigMapper;

  @Override public AlipayConfig getAlipayConfig(String appId, AlipayTypeCodes codes) {
    AlipayConfig alipayConfig;
    // 如果为空的话，使用第一个可用的支付方式
    if (StringUtils.isBlank(appId)) {
      if (AlipayTypeCodes.app_oauth.getCode().equalsIgnoreCase(codes.getCode())) {
        return this.getAppOauthDefAlipayConfig();
      } else if (AlipayTypeCodes.app_pay.getCode().equalsIgnoreCase(codes.getCode())) {
        return this.getAppPayDefAlipayConfig();
      } else {
        return this.getDefAlipayConfig();
      }
    }
    alipayConfig = (AlipayConfig) redisUtils.get(this.getAlipayRKey(appId));
    if (null != alipayConfig) {
      return alipayConfig;
    }
    Example example = new Example(AlipayConfig.class);
    Example.Criteria criteria = example.createCriteria();

    criteria.andEqualTo("appId", appId)
        .andEqualTo("status", YesOrNoCodes.YES.getCode());
    alipayConfig = alipayConfigMapper.selectOneByExample(example);
    if (null != alipayConfig) {
      redisUtils.set1Month(this.getAlipayRKey(appId), alipayConfig);
    }
    return alipayConfig;
  }

  @Override public AlipayConfig getAlipayConfig(String appId) {
    return this.getAlipayConfig(appId, AlipayTypeCodes.def_h5);
  }

  private AlipayConfig getDefAlipayConfig() {
    AlipayConfig alipayConfig =
        (AlipayConfig) redisUtils.get(RedisConstants.PAY_ALI_DEF_KEY);
    if (null != alipayConfig) {
      return alipayConfig;
    }
    Example example = new Example(AlipayConfig.class);
    example.createCriteria()
        .andEqualTo("isDef", YesOrNoCodes.YES.getCode())
        .andEqualTo("status", YesOrNoCodes.YES.getCode());
    example.orderBy("id").desc();
    alipayConfig = alipayConfigMapper.selectOneByExample(example);
    if (null != alipayConfig) {
      redisUtils.set1Month(RedisConstants.PAY_ALI_DEF_KEY, alipayConfig);
      redisUtils.set1Month(this.getAlipayRKey(alipayConfig.getAppId()), alipayConfig);
    }
    return alipayConfig;
  }

  private AlipayConfig getAppOauthDefAlipayConfig() {
    AlipayConfig alipayConfig;
    Example example = new Example(AlipayConfig.class);
    example.createCriteria()
        .andEqualTo("appOauthDef", YesOrNoCodes.YES.getCode())
        .andEqualTo("status", YesOrNoCodes.YES.getCode());
    example.orderBy("id").desc();
    alipayConfig = alipayConfigMapper.selectOneByExample(example);
    if (null != alipayConfig) {
      redisUtils.set1Month(RedisConstants.PAY_APP_OAUTH_DEF_KEY, alipayConfig);
      redisUtils.set1Month(this.getAlipayRKey(alipayConfig.getAppId()), alipayConfig);
    }
    return alipayConfig;
  }

  private AlipayConfig getAppPayDefAlipayConfig() {
    AlipayConfig alipayConfig;
    Example example = new Example(AlipayConfig.class);
    example.createCriteria()
        .andEqualTo("payAppDef", YesOrNoCodes.YES.getCode())
        .andEqualTo("status", YesOrNoCodes.YES.getCode());
    example.orderBy("id").desc();
    alipayConfig = alipayConfigMapper.selectOneByExample(example);
    if (null != alipayConfig) {
      redisUtils.set1Month(RedisConstants.PAY_APP_PAY_DEF_KEY, alipayConfig);
      redisUtils.set1Month(this.getAlipayRKey(alipayConfig.getAppId()), alipayConfig);
    }
    return alipayConfig;
  }

  private String getAlipayRKey(String appId) {
    return RedisConstants.PAY_ALI_KEY_PREFIX + appId;
  }
}
