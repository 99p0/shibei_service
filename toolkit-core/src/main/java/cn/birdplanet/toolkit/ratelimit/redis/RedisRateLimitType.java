package cn.birdplanet.toolkit.ratelimit.redis;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RedisRateLimitType
 * @description: redis 限流类型
 * @date 2019/11/20 16:08
 */
public enum RedisRateLimitType {

  /**
   * 自定义key
   */
  CUSTOMER,

  /**
   * 根据请求者IP + 请求的api
   */
  IP_URI,
  /**
   * 根据请求者IP + 请求的api + 用户UID
   */
  UID_IP_URI,
  /**
   * 根据请求者IP
   */
  IP;
}
