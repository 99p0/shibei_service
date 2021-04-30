/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.commons.base;

import cn.birdplanet.daka.infrastructure.commons.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * @author uncle.yang@outlook.com
 */
@Slf4j
public class BaseController {

  @Autowired public CacheManager cacheManager;
  @Autowired public RedisUtils redisUtils;

  @Qualifier(value = "taskExecutor")
  @Autowired public AsyncTaskExecutor taskExecutor;
}
