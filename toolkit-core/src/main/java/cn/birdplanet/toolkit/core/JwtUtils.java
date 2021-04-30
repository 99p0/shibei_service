/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: JwtUtils
 * @date 2019-07-26 10:02
 */
@Slf4j
public class JwtUtils {

  public static String build(String secret, String id, String sub, String iss, long ttlMillis) {

    //The JWT signature algorithm we will be using to sign the token
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    // 当前的时间戳
    long nowMillis = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    Date now = new Date(nowMillis);

    //We will sign our JWT with our ApiKey secret
    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    JwtBuilder builder = Jwts.builder()
        // jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
        .setId(id)
        // jwt签发者
        .setIssuer(iss)
        //
        .setSubject(sub)
        // jwt的签发时间
        .setIssuedAt(now)
        // 定义在什么时间之前，该jwt都是不可用的.
        .setNotBefore(now)
        .signWith(signatureAlgorithm, signingKey);

    // if it has been specified, let's add the expiration
    if (ttlMillis >= 0) {
      long expMillis = nowMillis + ttlMillis;
      Date exp = new Date(expMillis);
      // jwt的过期时间，这个过期时间必须要大于签发时间
      builder.setExpiration(exp);
    }
    //Builds the JWT and serializes it to a compact, URL-safe string
    return builder.compact();
  }

  public static Claims parse(String jwt, String secret) {
    Claims claims = null;
    try {
      claims = Jwts.parser()
          .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
          .parseClaimsJws(jwt).getBody();
    } catch (Exception e) {
      log.error("解析 Jwt 异常", e);
    }
    return claims;
  }
}
