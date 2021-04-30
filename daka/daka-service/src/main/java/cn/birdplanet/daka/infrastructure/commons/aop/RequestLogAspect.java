package cn.birdplanet.daka.infrastructure.commons.aop;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class RequestLogAspect {

  private final NamedThreadLocal<Long> ntl = new NamedThreadLocal<>("aop-req-log");

  @Pointcut("execution(public * com.birdplanet.controller..*.*(..))")
  public void logPointCut() {
  }

  @Before("logPointCut()")
  public void doBefore(JoinPoint joinPoint) throws Throwable {

    // 接收到请求，记录请求内容
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    //
    long tt = (long) request.getAttribute("ntl");
    ntl.set(tt);
    log.debug("{} >>> Request Start", tt);
    // 记录下请求内容
    log.debug("{} Request >>> {} \"{}\", params=({})", ntl.get(), request.getMethod(),
        request.getRequestURL(), Arrays.toString(joinPoint.getArgs()));
    //log.debug("{} Request IP >>> {}", ntl.get(), request.getRemoteAddr());
    //log.debug("{} Request ContentType >>> {}", ntl.get(), request.getContentType());
    //log.debug("{} Request Protocol >>> {}", ntl.get(), request.getProtocol());
  }

  /**
   * 返回通知
   *
   * returning的值和doAfterReturning的参数名一致
   *
   * @param ret 返回值
   * @throws Throwable
   */
  @AfterReturning(returning = "ret", pointcut = "logPointCut()")
  public void doAfterReturning(Object ret) throws Throwable {
    // 处理完请求，返回内容(返回值太复杂时，打印的是物理存储空间的地址)
    log.debug("{} Response Body >>> {}", ntl.get(), ret);
  }

  ///**
  // * 环绕操作
  // *
  // * 暂时用不到
  // *
  // * @throws Throwable
  // */
  //@Around("logPointCut()")
  //public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
  //  // ob 为方法的返回值
  //  Object ob = pjp.proceed();
  //  log.debug("{} Response Body >>> {}", ntl.get(), ob);
  //  return ob;
  //}
}
