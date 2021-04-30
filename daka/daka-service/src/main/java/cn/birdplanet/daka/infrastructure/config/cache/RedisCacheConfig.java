/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.config.cache;

import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RedisCacheConfig
 * @date 2019-07-19 02:37
 */
@Slf4j
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

  @Bean
  @Override
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> {
      StringBuilder sb = new StringBuilder(RedisConstants.CACHE_KEY_PREFIX);
      // 每个包名的第一个字母
      List<String> subPackageNameList =
          Arrays.stream(target.getClass().getPackage().getName().split("\\."))
              .map(name -> name.substring(0, 1))
              .collect(Collectors.toList());
      // 拼凑函数
      sb.append(Joiner.on(".").skipNulls().join(subPackageNameList) + "."
          + target.getClass().getSimpleName())
          .append(".")
          .append(method.getName())
          .append("(");
      // 函数的参数
      StringBuilder sb_p = new StringBuilder(30);
      if (params.length != 0) {
        for (Object obj : params) {
          sb_p.append(obj.toString()).append(",");
        }
        // 去除 循环体内的 最后的分隔号
        sb_p = (sb_p.length() > 1) ? sb_p.delete(sb_p.length() - 1, sb_p.length()) : sb_p;
      }
      sb.append(sb_p).append(")");
      return sb.toString();
    };
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    return new RedisCacheManager(
        RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
        // 默认策略，未配置的 key 会使用这个
        this.getRedisCacheConfigurationWithTtl(Duration.ofSeconds(600)),
        // 指定 key 策略
        this.getRedisCacheConfigurationMap()
    );
  }

  private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
    Map<String, RedisCacheConfiguration> map = Maps.newHashMapWithExpectedSize(5);
    // 永不过期 -1
    map.put("index", this.getRedisCacheConfigurationWithTtl(Duration.ofDays(30)));
    return map;
  }

  private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Duration duration) {
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(om);

    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
    redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
        RedisSerializationContext
            .SerializationPair
            .fromSerializer(jackson2JsonRedisSerializer)
    ).entryTtl(duration);

    return redisCacheConfiguration;
  }
}
