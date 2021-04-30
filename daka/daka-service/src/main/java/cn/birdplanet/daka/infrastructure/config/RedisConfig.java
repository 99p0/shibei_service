/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by dwy on 2016/8/5.
 */
@Slf4j
@Configuration
@AutoConfigureAfter
public class RedisConfig {

  @Autowired
  private RedisTemplate redisTemplate;

  @Bean
  public RedisTemplate redisTemplate() {
    log.info("RedisTemplate init...");
    RedisTemplate template = new RedisTemplate();
    template.setConnectionFactory(redisTemplate.getConnectionFactory());
    //设置序列化工具，这样Bean不需要实现Serializable接口
    this.setSerializer(template);
    template.afterPropertiesSet();
    return template;
  }

  private void setSerializer(RedisTemplate template) {
    template.setEnableDefaultSerializer(true);
    template.setDefaultSerializer(this.valueSerializer());
    template.setValueSerializer(this.valueSerializer());
    template.setKeySerializer(this.keySerializer());
  }

  private RedisSerializer<String> keySerializer() {
    return new StringRedisSerializer();
  }

  private RedisSerializer<Object> valueSerializer() {
    ObjectMapper om = new ObjectMapper();
    //
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    // 忽略目标对象没有的属性
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    om.findAndRegisterModules();
    return new GenericJackson2JsonRedisSerializer(om);
  }
}
