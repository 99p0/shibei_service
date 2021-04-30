/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.config;

import cn.birdplanet.daka.infrastructure.interceptor.BlocklistInterceptor;
import cn.birdplanet.daka.infrastructure.interceptor.JwtTokenForConsoleInterceptor;
import cn.birdplanet.daka.infrastructure.interceptor.JwtTokenForPunchInterceptor;
import cn.birdplanet.daka.infrastructure.interceptor.ParameterInterceptor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dwy
 * @date 2016/8/5
 */
@Slf4j
@Configuration
@AutoConfigureAfter
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean public JwtTokenForPunchInterceptor jwtTokenForPunchInterceptor() {
    return new JwtTokenForPunchInterceptor();
  }

  @Bean public JwtTokenForConsoleInterceptor jwtTokenForConsoleInterceptor() {
    return new JwtTokenForConsoleInterceptor();
  }

  @Bean
  public ParameterInterceptor parameterInterceptor() {
    return new ParameterInterceptor();
  }

  @Bean
  public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
    return new DeviceResolverHandlerInterceptor();
  }

  @Bean
  public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
    return new DeviceHandlerMethodArgumentResolver();
  }

  @Bean public BlocklistInterceptor blocklistInterceptor() {
    return new BlocklistInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    log.debug("interceptors init...");

    // 设备解析
    registry.addInterceptor(deviceResolverHandlerInterceptor());

    // 查看返回参数信息 > 输出参数 》 并校验参数
    registry.addInterceptor(this.parameterInterceptor()).excludePathPatterns("/error");

    //token web > 拦截链接
    registry.addInterceptor(this.jwtTokenForPunchInterceptor())
        .addPathPatterns("/punch/**")
        .excludePathPatterns("/error", "/open/**", "/punch/oauth/**", "/punch/callback/**",
            "/punch/notify/**");

    //token web > 参加 黑名单
    //registry.addInterceptor(this.blocklistInterceptor())
    //    .addPathPatterns("punch/game-mode/activity/join", "punch/game-mode/activity/next-round",
    //        "punch/room-mode/join", "punch/normal-mode/join");

    //token console > 拦截链接
    registry.addInterceptor(this.jwtTokenForConsoleInterceptor())
        .addPathPatterns("/console/**")
        .excludePathPatterns("/error", "/console/login");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/public/");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(deviceHandlerMethodArgumentResolver());
  }
}
