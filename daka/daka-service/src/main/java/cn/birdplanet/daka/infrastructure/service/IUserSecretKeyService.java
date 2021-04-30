package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.UserSecretKey;

public interface IUserSecretKeyService {

  UserSecretKey getUserSecretKeyIfNullCreateByUid(long uid);
}
