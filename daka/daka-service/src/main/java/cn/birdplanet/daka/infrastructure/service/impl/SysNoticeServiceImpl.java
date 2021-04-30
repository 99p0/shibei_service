/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.SysNotice;
import cn.birdplanet.daka.infrastructure.persistence.punch.SysNoticeMapper;
import cn.birdplanet.daka.infrastructure.service.ISysNoticeService;
import cn.birdplanet.toolkit.extra.code.ModeSimpleCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: UidBuilderServiceImpl
 * @date 2019-05-08 19:32
 */
@Slf4j
@Service
public class SysNoticeServiceImpl extends BaseService implements ISysNoticeService {

  @Autowired private SysNoticeMapper sysNoticeMapper;

  @Override public List<SysNotice> getNoticeForGame() {
    List<SysNotice> list = (List<SysNotice>) redisUtils.get(RedisConstants.SYSTEM_NOTICE_GAME);
    log.debug(">>> game notice:{}", list);
    // 防止没有消息，会一直刷库
    if (null == list) {
      Example example = new Example(SysNotice.class);
      example.createCriteria()
          .andEqualTo("mode", "G")
          .andEqualTo("type", "A")
          .andEqualTo("status", YesOrNoCodes.YES.getCode());
      list = sysNoticeMapper.selectByExample(example);
      // 可以存入空集合
      if (null != list) {
        redisUtils.set1Week(RedisConstants.SYSTEM_NOTICE_GAME, list);
      }
    }
    return list;
  }

  @Override public List<SysNotice> getSysNoticeWithType(ModeSimpleCodes codes, String type) {
    String key = RedisConstants.SYS_NOTICE_KEY_PREFIX + codes.getCode() + ":" + type;
    List<SysNotice> list = (List<SysNotice>) redisUtils.get(key);
    log.debug(">>>{}:{} sys notice:{}", codes, type, list);
    // 防止没有消息，会一直刷库
    if (null == list) {
      Example example = new Example(SysNotice.class);
      example.createCriteria()
          .andEqualTo("mode", codes.getCode())
          .andEqualTo("type", type)
          .andEqualTo("status", YesOrNoCodes.YES.getCode());
      list = sysNoticeMapper.selectByExample(example);
      // 可以存入空集合
      if (null != list) {
        redisUtils.set1Week(key, list);
      }
    }
    return list;
  }

  @Override public String getSysNoticeStrWithType(ModeSimpleCodes codes, String type) {
    List<SysNotice> list = this.getSysNoticeWithType(ModeSimpleCodes.GameMode, type);
    // 将集合转换成字符
    String msg = list.isEmpty() ? "" : IntStream.range(0, list.size())
        .mapToObj(i -> (i + 1) + "、" + list.get(i).getContent() + "；")
        .collect(Collectors.joining());
    return msg;
  }
}
