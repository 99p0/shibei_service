/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.interceptor;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class ParameterInterceptor extends HandlerInterceptorAdapter {

  private final NamedThreadLocal<Long> ntl = new NamedThreadLocal<>("interceptor-req-log");

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    long ctm = System.currentTimeMillis();
    ntl.set(ctm);
    log.debug("{} Request >>> {} {} \"{}\"", ntl.get(), request.getProtocol(),
        request.getMethod(), request.getRequestURL());
    log.debug("{} Request IP >>> {}", ntl.get(), request.getRemoteAddr());
    log.debug("{} Request ContentType >>> {}", ntl.get(), request.getContentType());
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = (String) headerNames.nextElement();
      log.debug("{} Request header > {} >>> {}", ntl.get(), key, request.getHeader(key));
    }
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String key = (String) parameterNames.nextElement();
      log.debug("{} Request parameter > {} >>> {}", ntl.get(), key, request.getParameter(key));
    }
    // 放在request中 后续 方便处理
    request.setAttribute("ntl", ntl.get());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    super.afterCompletion(request, response, handler, ex);
    log.debug("{} Request Time Consuming {}ms, {}", ntl.get(),
        (System.currentTimeMillis() - ntl.get()), request.getRequestURL());
  }
}
