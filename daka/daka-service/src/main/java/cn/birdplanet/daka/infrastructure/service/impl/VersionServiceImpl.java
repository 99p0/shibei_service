package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.AppVersion;
import cn.birdplanet.daka.infrastructure.persistence.punch.AppVersionMapper;
import cn.birdplanet.daka.infrastructure.service.IVersionService;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service
public class VersionServiceImpl extends BaseService implements IVersionService {

  @Autowired private AppVersionMapper appVersionMapper;

  @Override public AppVersion getCurrVersion(String os) {
    if (StringUtils.isBlank(os)) {
      return null;
    }
    String rkey = RedisConstants.APP_VERSION_KEY_PREFIX + os.toLowerCase();
    AppVersion version = (AppVersion) redisUtils.get(rkey);
    if (null == version) {
      synchronized (this) {
        if (null == version) {
          version = this.getVersion(os);
          if (null == version) {
            redisUtils.set1Day(rkey, new AppVersion("1.0.0"));
          } else {
            redisUtils.set1Month(rkey, version);
          }
        }
      }
    }
    return version;
  }

  private AppVersion getVersion(String os) {
    if (StringUtils.isBlank(os)) {
      return null;
    }
    Example example = new Example(AppVersion.class);
    Example.Criteria criteria = example.createCriteria();
    if (StringUtils.isNotBlank(os)) {
      criteria.andEqualTo("os", os.toLowerCase());
    }
    criteria.andEqualTo("status", YesOrNoCodes.YES.getCode());
    AppVersion version = appVersionMapper.selectOneByExample(example);
    return version;
  }
}
