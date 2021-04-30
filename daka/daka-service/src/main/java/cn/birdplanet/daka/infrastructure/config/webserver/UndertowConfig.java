///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package cn.birdplanet.config.webserver;
//
//import io.undertow.UndertowOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@AutoConfigureAfter
//public class UndertowConfig {
//
//  @Bean
//  public ServletWebServerFactory servletWebServerFactory() {
//    log.info("Undertow 启动 HTTP2");
//    UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
//    // 开启 undertow http2
//    factory.addBuilderCustomizers(
//        builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
//    return factory;
//  }
//}
