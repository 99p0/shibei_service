package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.AppVersion;

public interface IVersionService {

  AppVersion getCurrVersion(String os);
}
