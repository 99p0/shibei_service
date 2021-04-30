package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.UserSecretKey;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserSecretKeyMapper;
import cn.birdplanet.daka.infrastructure.service.IUserSecretKeyService;
import cn.birdplanet.toolkit.crypto.Base64;
import cn.birdplanet.toolkit.crypto.rsa.RSA2;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service
public class UserSecretKeyServiceImpl extends BaseService implements IUserSecretKeyService {

  @Autowired private UserSecretKeyMapper secretKeyMapper;

  private synchronized UserSecretKey createRsaByUid(long uid) {
    Example example = new Example(UserSecretKey.class);
    example.createCriteria().andEqualTo("uid", uid).andEqualTo("status", 1);
    UserSecretKey secretKey = secretKeyMapper.selectOneByExample(example);
    if (secretKey == null) {
      // 不存在的话， 需要重新生成该用户的密钥
      // 公私钥对
      Map<String, byte[]> keyMap = RSA2.generateKeyBytes();
      String publicKey = Base64.encode(keyMap.get(RSA2.PUBLIC_KEY));
      log.debug("验签公钥==>{}", publicKey);

      String privateKey = Base64.encode(keyMap.get(RSA2.PRIVATE_KEY));
      log.debug("签名私钥==>{}", privateKey);
      // 是否存在，有的话，直接返回，没有生成
      secretKey = new UserSecretKey(uid, publicKey, privateKey);
      secretKeyMapper.insertSelective(secretKey);
    }
    return secretKey;
  }

  @Override public UserSecretKey getUserSecretKeyIfNullCreateByUid(long uid) {
    Example example = new Example(UserSecretKey.class);
    example.createCriteria().andEqualTo("uid", uid).andEqualTo("status", 1);
    UserSecretKey secretKey = secretKeyMapper.selectOneByExample(example);
    if (secretKey == null) {
      // 不存在的话， 需要重新生成该用户的密钥
      secretKey = this.createRsaByUid(uid);
    }
    return secretKey;
  }
}
