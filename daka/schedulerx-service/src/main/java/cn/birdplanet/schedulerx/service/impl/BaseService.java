/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.schedulerx.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: BaseService
 * @date 2019-06-06 16:16
 */
@Slf4j
public class BaseService {

  @Autowired protected CacheManager cacheManager;
  @Autowired protected RedisUtils redisUtils;

  @Qualifier(value = "taskExecutor")
  @Autowired protected AsyncTaskExecutor taskExecutor;
}
