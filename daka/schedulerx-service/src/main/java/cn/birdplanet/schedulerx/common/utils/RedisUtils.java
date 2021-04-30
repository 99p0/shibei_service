package cn.birdplanet.schedulerx.common.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RedisUtils
 * @date 2019/11/27 14:48
 */
@Slf4j
@Component
public class RedisUtils {

  @Autowired private RedisTemplate redisTemplate;

  /**
   * 指定缓存失效时间
   *
   * @param key  键
   * @param time 时间(秒)
   */
  public boolean setExpire(String key, long time) {
    return this.setExpire(key, time, TimeUnit.SECONDS);
  }

  public boolean setExpire(String key, long time, TimeUnit timeUnit) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, timeUnit);
      }
      return true;
    } catch (Exception e) {
      log.error(key, e);
      return false;
    }
  }

  /**
   * 根据key 获取过期时间
   */
  public long getExpire(String key) {
    return this.getExpire(key, TimeUnit.SECONDS);
  }

  public long getExpire(String key, TimeUnit timeUnit) {
    return redisTemplate.getExpire(key, timeUnit);
  }

  /**
   * 判断key是否存在
   *
   * @param key 键
   * @return true 存在 false不存在
   */
  public boolean hasKey(String key) {
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      log.error(key, e);
      return false;
    }
  }

  public void del(String... keys) {
    if (keys != null && keys.length > 0) {
      if (keys.length == 1) {
        redisTemplate.delete(keys[0]);
      } else {
        redisTemplate.delete(CollectionUtils.arrayToList(keys));
      }
    }
  }

  /**
   * 根据key获取值
   *
   * @param key 键
   * @return 值
   */
  public Object get(String key) {
    return key == null ? null : redisTemplate.opsForValue().get(key);
  }

  public boolean set1Hours(String key, Object value) {
    return this.set(key, value, 1, TimeUnit.HOURS);
  }

  public boolean set1Day(String key, Object value) {
    return this.set(key, value, 1, TimeUnit.DAYS);
  }

  public boolean set2Day(String key, Object value) {
    return this.set(key, value, 2, TimeUnit.DAYS);
  }

  public boolean set1Week(String key, Object value) {
    return this.set(key, value, 7, TimeUnit.DAYS);
  }

  public boolean set1Month(String key, Object value) {
    return this.set(key, value, 30, TimeUnit.DAYS);
  }

  public boolean setMonths(String key, Object value, int months) {
    return this.set(key, value, 30 * months, TimeUnit.DAYS);
  }

  /**
   * 将值放入缓存并设置时间
   *
   * @param key   键iq1123334588==
   * @param value 值
   * @param time  时间(秒) -1为无期限
   * @return true成功 false 失败
   */
  public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
    try {
      if (time > 0) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
      } else {
        redisTemplate.opsForValue().set(key, value, 7, TimeUnit.DAYS);
      }
      return true;
    } catch (Exception e) {
      log.error(key, e);
      return false;
    }
  }

  public boolean hash_hasKey(String key, Object hashKey) {
    return redisTemplate.opsForHash().hasKey(key, hashKey);
  }

  public long hash_len(String key) {
    return redisTemplate.opsForHash().size(key);
  }

  public List<?> hash_values(String key) {
    return redisTemplate.opsForHash().values(key);
  }

  public Map hash_entries(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  public Object hash_get(String key, Object hashKey) {
    return redisTemplate.opsForHash().get(key, hashKey);
  }

  public boolean hash_put(String key, Object hashKey, Object hashVal, final long timeout,
      final TimeUnit unit) {
    try {
      redisTemplate.opsForHash().put(key, hashKey, hashVal);
      redisTemplate.expire(key, timeout, unit);
      return true;
    } catch (Exception e) {
      log.error(key, e);
      return false;
    }
  }

  public boolean hash_putAll(String key, Map dataMap, final long timeout, final TimeUnit unit) {
    try {
      redisTemplate.opsForHash().putAll(key, dataMap);
      redisTemplate.expire(key, timeout, unit);
      return true;
    } catch (Exception e) {
      log.error(key, e);
      return false;
    }
  }

  public boolean expire(String key, long time, TimeUnit timeUnit) {
    return redisTemplate.expire(key, time, timeUnit);
  }
}
