/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.interceptor;

import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.util.IpUtil;
import cn.birdplanet.daka.infrastructure.commons.util.RedisUtils;
import cn.birdplanet.daka.infrastructure.commons.util.ResponseUtils;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * @author dwy
 */
@Slf4j
public class BlocklistInterceptor implements AsyncHandlerInterceptor {

  @Autowired private RedisUtils redisUtils;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    log.debug("BlocklistInterceptor:: start...");
    // 获取存储的用户信息
    UserDtlVO userDtlVo = (UserDtlVO) request.getAttribute("currUserDtlVo");
    request.setAttribute("currUserDtlVo", userDtlVo);
    String currIp = IpUtil.getIpAddr(request);
    log.debug("当前请求的IP/用户 {}/{}", currIp, userDtlVo);
    // ip黑名单
    List<String> ipBlocklist = (List<String>) redisUtils.get(RedisConstants.BLOCKLIST_IP);
    if (ipBlocklist != null && ipBlocklist.contains(currIp)) {
      return ResponseUtils.output(response, ErrorCodes.blocklist_ip);
    }
    // 用户黑名单
    List<Long> uidBlocklist = (List<Long>) redisUtils.get(RedisConstants.BLOCKLIST_UID);
    if (uidBlocklist != null && uidBlocklist.contains(
        null != userDtlVo ? userDtlVo.getUid() : 0L)) {
      return ResponseUtils.output(response, ErrorCodes.blocklist_uip);
    }
    return true;
  }
}
