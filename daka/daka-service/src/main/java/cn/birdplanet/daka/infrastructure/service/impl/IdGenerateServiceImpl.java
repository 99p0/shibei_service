/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.SN;
import cn.birdplanet.daka.infrastructure.persistence.punch.SnMapper;
import cn.birdplanet.daka.infrastructure.service.IIdGenerateService;
import cn.birdplanet.toolkit.core.idwork.SnowflakeUtils;
import cn.birdplanet.toolkit.extra.code.IdGenerateCodes;
import com.google.common.collect.Maps;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: UidBuilderServiceImpl
 * @date 2019-05-08 19:32
 */
@Slf4j
@Service
public class IdGenerateServiceImpl implements IIdGenerateService {

  @Autowired private SnMapper snMapper;

  private final HashMap<Integer, SnowflakeUtils> idWorkerHashMap = Maps.newHashMapWithExpectedSize(4);

  @Override
  public synchronized long nextUid() {
    SN sn = new SN(LocalDateTime.now());
    snMapper.insert(sn);
    return sn.getId();
  }

  @Override
  public long ordersn() {
    return this.nextId(IdGenerateCodes.ordersn);
  }

  @Override
  public synchronized long nextId(IdGenerateCodes idCode) {
    SnowflakeUtils idWorker;
    if (null != idCode && idWorkerHashMap.containsKey(idCode.getCode())) {
      idWorker = idWorkerHashMap.get(idCode);
    } else {
      idWorker = new SnowflakeUtils(0, 0);
    }
    return idWorker.nextId();
  }

  @Override
  public synchronized long nextId() {
    return this.nextId(null);
  }

  @Override
  public Map<String, Object> parse(long uid) {
    return idWorkerHashMap.get(IdGenerateCodes.uid).parse(uid);
  }
}
