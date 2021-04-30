/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.SysNotice;
import cn.birdplanet.toolkit.extra.code.ModeSimpleCodes;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: ISysNoticeService
 * @date 2019-06-06 16:15
 */
public interface ISysNoticeService {

  List<SysNotice> getNoticeForGame();

  List<SysNotice> getSysNoticeWithType(ModeSimpleCodes codes, String type);
  String getSysNoticeStrWithType(ModeSimpleCodes codes, String type);
}
