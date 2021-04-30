/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.commons.util;

import cn.birdplanet.daka.infrastructure.config.BirdplanetConfig;
import cn.birdplanet.toolkit.core.JwtUtils;
import cn.birdplanet.toolkit.extra.code.AdminTypeCodes;
import cn.birdplanet.toolkit.extra.code.UserTypeCodes;
import io.jsonwebtoken.Claims;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: JwtUtils
 * @date 2019-07-26 10:02
 */
@Slf4j
public class JwtTokenUtils {

  private static final String ISS = "Birdplanet Icn.";

  public static String buildForConsole(long uid, String utype, long ttlMillis) {
    return JwtUtils.build(BirdplanetConfig.secretJwtConsole, String.valueOf(uid),
        AdminTypeCodes.codeOf(utype).getDesc(), ISS, ttlMillis);
  }

  public static String build(long uid, String utype, long ttlMillis) {
    return JwtUtils.build(BirdplanetConfig.secretJwt, String.valueOf(uid),
        UserTypeCodes.codeOf(utype).getDesc(), ISS, ttlMillis);
  }

  public static String build(String sessionId, String sub, long ttlMillis) {
    return JwtUtils.build(BirdplanetConfig.secretJwt, sessionId, sub, ISS, ttlMillis);
  }

  public static boolean isExpiration(String token) {
    return parse(token).getExpiration().before(new Date());
  }

  public static Claims parse(String jwt) {
    return JwtUtils.parse(jwt, BirdplanetConfig.secretJwt);
  }
}

