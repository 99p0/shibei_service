///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.http2.Http2Protocol;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@AutoConfigureAfter
//public class TomcatConfig {
//
//  @Bean
//  public ServletWebServerFactory tomcatContainer() {
//    log.info("tomcat 配置...");
//    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//    log.info("tomcat 使用nio2协议");
//    factory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
//    log.info("tomcat 启动HTTP2");
//    factory.addConnectorCustomizers(connector -> connector.addUpgradeProtocol(new Http2Protocol()));
//    return factory;
//  }
//}
