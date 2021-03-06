///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: WebSecurityConfig
// * @date 2019-07-22 05:53
// */
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//  @Autowired
//  private UserDetailsService userDetailsService;
//
//  @Autowired
//  public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder)
//      throws Exception {
//    authenticationManagerBuilder
//        // 设置UserDetailsService
//        .userDetailsService(this.userDetailsService)
//        // 使用BCrypt进行密码的hash
//        .passwordEncoder(passwordEncoder());
//  }
//
//  // 装载BCrypt密码编码器
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }
//
//  @Override
//  protected void configure(HttpSecurity httpSecurity) throws Exception {
//    httpSecurity
//        // 由于使用的是JWT，我们这里不需要csrf
//        .csrf().disable()
//
//        // 基于token，所以不需要session
//        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        .and()
//        // 对请求进行认证
//        .authorizeRequests()
//        //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//        // 允许对于网站静态资源的无授权访问
//        .antMatchers(
//            HttpMethod.GET,
//            "/",
//            "/*.html",
//            "/favicon.ico",
//            "/**/*.html",
//            "/**/*.css",
//            "/**/*.js"
//        ).permitAll()
//        // 对于获取token的rest api要允许匿名访问
//        .antMatchers("/auth/**").permitAll()
//        // 除上面外的所有请求全部需要鉴权认证
//        .anyRequest().authenticated();
//
//    // 禁用缓存
//    httpSecurity.headers().cacheControl();
//  }
//}
