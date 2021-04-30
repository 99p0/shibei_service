package cn.birdplanet.daka.infrastructure.commons.aop.ratelimit;

import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.util.IpUtil;
import cn.birdplanet.daka.infrastructure.commons.util.ResponseUtils;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.ratelimit.redis.RedisRateLimit;
import cn.birdplanet.toolkit.ratelimit.redis.RedisRateLimitType;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RateLimitAspect
 * @date 2019/11/20 15:31
 */

@Slf4j
@Aspect
@Component
public class RedisRateLimitAspect {

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private HttpServletResponse response;
  @Autowired private HttpServletRequest request;

  @Pointcut("@annotation(cn.birdplanet.toolkit.ratelimit.redis.RedisRateLimit)")
  public void pointcut() {
  }

  @Around("pointcut()")
  public Object interceptor(ProceedingJoinPoint pjp) {
    Object obj = null;
    //获取连接点的方法签名对象
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    //获取方法实例
    Method method = signature.getMethod();
    //获取注解实例
    RedisRateLimit limitAnnotation = method.getAnnotation(RedisRateLimit.class);
    //注解中的类型
    RedisRateLimitType limitType = limitAnnotation.limitType();
    //获取key名称
    String name = limitAnnotation.name();
    String key;
    //获取限制时间范围
    int limitPeriod = limitAnnotation.period();
    //获取限制访问次数
    int limitCount = limitAnnotation.count();

    switch (limitType) {
      // 如果类型是IP，则根据IP限制访问次数，key取IP地址
      case IP:
        //HttpServletRequest request =
        //    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        key = IpUtil.getIpAddr(request);
        break;
      case IP_URI:
        key = IpUtil.getIpAddr(request) + "_" + request.getRequestURI();
        break;
      case UID_IP_URI:
        // 拦截器中已经存储
        UserDtlVO currUserDtlVo = null;
        try {
          currUserDtlVo = (UserDtlVO) request.getAttribute("currUserDtlVo");
        } catch (Exception e) {
          log.error("未查询到用户信息");
        }
        key = null == currUserDtlVo ? "00000" : currUserDtlVo.getUid()
            + "_"
            + IpUtil.getIpAddr(request)
            + "_"
            + request.getRequestURI();
        break;
      // 如果类型是customer，则根据key限制访问次数
      case CUSTOMER:
        key = limitAnnotation.key();
        break;
      // 否则按照方法名称限制访问次数
      default:
        key = StringUtils.upperCase(method.getName());
    }
    ImmutableList<String> keys = ImmutableList.of(StringUtils.join(limitAnnotation.prefix(), key));
    try {
      String luaScript = buildLuaScript();
      RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
      Long count = (Long) redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);

      if (log.isDebugEnabled()) {
        log.debug("Access try count is {} for name={} and key = {}", count, name, key);
      }
      if (count != null && count.intValue() <= limitCount) {
        obj = pjp.proceed();
        return obj;
      } else {
        log.error("访问超限");
        ResponseUtils.output(response, ErrorCodes.rate_limiter);
      }
    } catch (Throwable e) {
      log.error("redis限流异常", e);
      ResponseUtils.output(response, ErrorCodes.rate_limiter);
    }
    return obj;
  }

  /**
   * 限流 脚本 （计数器方式）
   *
   * @return lua脚本
   */
  public String getLimitLuaScript() {

    InputStream stream = RedisRateLimitAspect.class.getClassLoader()
        .getResourceAsStream("scripts/redis_access_limit.lua");
    StringBuilder buffer = new StringBuilder();
    byte[] bytes = new byte[1024];
    try {
      for (int n; (n = stream.read(bytes)) != -1; ) {
        buffer.append(new String(bytes, 0, n));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return buffer.toString();
  }

  public String buildLuaScript() {
    StringBuilder lua = new StringBuilder();
    lua.append("local c");
    lua.append("\nc = redis.call('get',KEYS[1])");
    // 调用不超过最大值，则直接返回
    lua.append("\nif c and tonumber(c) > tonumber(ARGV[1]) then");
    lua.append("\nreturn c;");
    lua.append("\nend");
    // 执行计算器自加
    lua.append("\nc = redis.call('incr',KEYS[1])");
    lua.append("\nif tonumber(c) == 1 then");
    // 从第一次调用开始限流，设置对应键值的过期
    lua.append("\nredis.call('expire',KEYS[1],ARGV[2])");
    lua.append("\nend");
    lua.append("\nreturn c;");
    return lua.toString();
  }
}
