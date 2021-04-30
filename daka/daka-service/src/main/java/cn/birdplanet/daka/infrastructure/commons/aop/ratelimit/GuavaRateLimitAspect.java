package cn.birdplanet.daka.infrastructure.commons.aop.ratelimit;

import cn.birdplanet.daka.infrastructure.commons.util.ResponseUtils;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.ratelimit.guava.GuavaRateLimit;
import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: GuavaRateLimitAspect
 * @date 2019/11/20 15:31
 */

@Slf4j
@Component
@Scope
@Aspect
public class GuavaRateLimitAspect {

  /**
   * 用来存放不同接口的RateLimiter(key为接口名称，value为RateLimiter)
   */
  private final ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();

  private RateLimiter rateLimiter;

  @Autowired private HttpServletResponse response;

  @Pointcut("@annotation(cn.birdplanet.toolkit.ratelimit.guava.GuavaRateLimit)")
  public void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
    Object obj = null;
    //获取拦截的方法名
    MethodSignature msig = (MethodSignature) joinPoint.getSignature();
    //获取注解信息
    GuavaRateLimit annotation = msig.getMethod().getAnnotation(GuavaRateLimit.class);
    //获取注解每秒加入桶中的token
    double limitNum = annotation.limitNum();
    // 注解所在方法名区分不同的限流策略
    String functionName = msig.getName();

    //获取rateLimiter
    if (map.containsKey(functionName)) {
      rateLimiter = map.get(functionName);
    } else {
      map.put(functionName, RateLimiter.create(limitNum));
      rateLimiter = map.get(functionName);
    }

    if (rateLimiter.tryAcquire()) {
      // 执行方法
      try {
        obj = joinPoint.proceed();
      } catch (Throwable throwable) {
        log.error("guava限流异常", throwable);
        ResponseUtils.output(response, ErrorCodes.rate_limiter);
      }
    } else {
      // 拒绝了请求（服务降级）
      ResponseUtils.output(response, ErrorCodes.rate_limiter);
    }
    return obj;
  }
}
