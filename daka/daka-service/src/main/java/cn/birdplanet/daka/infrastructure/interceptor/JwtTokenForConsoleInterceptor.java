/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.interceptor;

import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.commons.util.JwtTokenUtils;
import cn.birdplanet.daka.infrastructure.commons.util.RedisUtils;
import cn.birdplanet.daka.infrastructure.commons.util.ResponseUtils;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class JwtTokenForConsoleInterceptor extends HandlerInterceptorAdapter {

  @Autowired private RedisUtils redisUtils;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String token = request.getHeader(BirdplanetConstants.TOKEN_KEY);
    log.debug("token >>> {}", token);
    if (StringUtils.isBlank(token) || !token.startsWith(BirdplanetConstants.TOKEN_VALUE_PREFIX)) {
      return ResponseUtils.output(response, ErrorCodes.token_err);
    }
    //
    token = token.replace(BirdplanetConstants.TOKEN_VALUE_PREFIX, "");
    Claims claims = JwtTokenUtils.parse(token);
    if (null == claims) {
      return ResponseUtils.output(response, ErrorCodes.token_err);
    }
    String key = RedisConstants.ADMIN_KEY_PREFIX + claims.getId();
    // 是否有效 > 超时
    if (redisUtils.getExpire(key) <= 0) {
      redisUtils.del(key);
      return ResponseUtils.output(response, ErrorCodes.token_err);
    }
    // jwt 有效期
    if (JwtTokenUtils.isExpiration(token)) {
      return ResponseUtils.output(response, ErrorCodes.token_err);
    }
    Admin currAdmin = (Admin) redisUtils.get(key);
    // 是否存在
    if (null == currAdmin) {
      return ResponseUtils.output(response, ErrorCodes.token_err);
    }
    request.setAttribute("currAdmin", currAdmin);
    log.debug("当前请求的管理员: {}", currAdmin);
    return true;
  }
}
