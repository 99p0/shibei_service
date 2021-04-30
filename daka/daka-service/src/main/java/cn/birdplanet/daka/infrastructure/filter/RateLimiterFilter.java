/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.filter;

import cn.birdplanet.daka.infrastructure.commons.util.ResponseUtils;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import com.google.common.util.concurrent.RateLimiter;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(1)
@WebFilter(urlPatterns = {"/*"}, filterName = "rateLimiterFilter")
public class RateLimiterFilter implements Filter {

  private static final double permitsPerSecond = 1000.00D;
  private RateLimiter limiter = null;

  @Override
  public void init(FilterConfig filterConfig) {
    log.debug("init rateLimiter, {} request per second", permitsPerSecond);
    limiter = RateLimiter.create(permitsPerSecond);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse resp = (HttpServletResponse) response;
    HttpServletRequest req = (HttpServletRequest) request;
    String sessionId = req.getSession().getId();
    if (limiter.tryAcquire()) {
      log.debug("Allow access >>> {} {} \"{}\" sessionId：{}", req.getProtocol(), req.getMethod(), req.getRequestURL(),sessionId);
      chain.doFilter(request, response);
    } else {
      log.debug("Limit access >>> {} \"{}\"", req.getMethod(), req.getRequestURL());
      try {
        ResponseUtils.output(resp, ErrorCodes.rate_limiter);
      } catch (Exception e) {
        log.error("access error {} >>> {} >>> {}", req.getRemoteHost(), req.getRequestURI(), e);
      }
      return;
    }
  }
}
