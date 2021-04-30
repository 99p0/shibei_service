/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: CorsConfig
 * @date 2019-05-27 23:03
 */
@Configuration
public class CorsConfig {

  private CorsConfiguration buildConfig() {
    CorsConfiguration config = new CorsConfiguration();
    // 当设置了allowCredentials=true的时候，服务器端响应的Access-Control-Allow-Origin头，它的值不能是*，必须要明确的指定出客户端的origin。
    // 是否支持安全证书
    //config.setAllowCredentials(true);
    // 设置你要允许的网站域名，如果全允许则设为 *
    config.addAllowedOrigin("*");
    // 如果要限制 HEADER 或 METHOD 请自行更改
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    return config;
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 配置所有请求
    source.registerCorsConfiguration("/**", this.buildConfig());
    return new CorsFilter(source);

    // 这个顺序很重要哦，为避免麻烦请设置在最前
    //FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    //bean.setOrder(0);
    //return bean;
  }
}
