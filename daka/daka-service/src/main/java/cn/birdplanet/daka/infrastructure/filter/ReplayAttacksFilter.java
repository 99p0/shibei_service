///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.filter;
//
//import com.birdplanet.commons.code.ErrorCodes;
//import com.birdplanet.commons.util.RedisUtils;
//import com.birdplanet.commons.util.ResponseUtils;
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//
///**
// * 基于timestamp和nonce的方案
// *
// * nonce的一次性可以解决timestamp参数60s的问题，timestamp可以解决nonce参数“集合”越来越大的问题。
// */
//@Slf4j
//@Order(2)
//@WebFilter(urlPatterns = {"/*"}, filterName = "replayAttacksFilter")
//public class ReplayAttacksFilter implements Filter {
//
//  @Autowired protected RedisUtils redisUtils;
//
//  @Override
//  public void init(FilterConfig filterConfig) {
//    log.debug("replayAttacksFilter init...");
//  }
//
//
//
//  @Override
//  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//      throws IOException, ServletException {
//    String nonceKey = "nonce".intern();
//
//    HttpServletResponse resp = (HttpServletResponse) response;
//    HttpServletRequest req = (HttpServletRequest) request;
//    // 使用封装的request， 防止request的流只能读取一次
//    log.debug("{} {} \"{}\"", req.getProtocol(), req.getMethod(), req.getRequestURL());
//
//    String _timestamp = req.getHeader("timestamp");
//    String nonce = req.getHeader(nonceKey);
//    String signature = req.getHeader("signature");
//
//    // 请求头参数非空验证
//    if (!StringUtils.isNumeric(_timestamp)
//        || StringUtils.isEmpty(nonce)
//        || StringUtils.isEmpty(signature)) {
//      ResponseUtils.output(resp, ErrorCodes.params_err);
//      return;
//    }
//
//    // 请求时间和现在时间对比验证，发起请求时间和服务器时间不能超过timeLimit秒
//    // 时间限制配置 毫秒
//    int timeLimit = 60000;
//    // 当前时间戳
//    long gmt = System.currentTimeMillis();
//    // (一): 请求时间超出时间范围的将被拒绝.
//    if (gmt - Long.parseLong(_timestamp) > timeLimit) {
//      ResponseUtils.output(resp, ErrorCodes.access_timeout);
//      return;
//    }
//
//    // (二): 缓存过期时间等于有效时间的跨度, 若缓存中已存在该随机数, 则拒绝.
//    // 如果nonce没有在缓存中，则需要加入，并设置过期时间为timeLimit秒
//    if (redisUtils.hash_hasKey(nonceKey, nonce)) {
//      ResponseUtils.output(resp, ErrorCodes.access_replay);
//      return;
//    } else {
//      // 没有使用，存储起来，设置过期时间为当前访问时间+判断请求过期的时间
//      // 这样就不用定时任务去清除之前的nonce，利用redis自动清除
//      redisUtils.hash_put(nonceKey, nonce, nonce,1, TimeUnit.DAYS);
//      // 服务器生成签名与header中签名对比
//      // autograph : signature
//      chain.doFilter(request, response);
//    }
//  }
//}
