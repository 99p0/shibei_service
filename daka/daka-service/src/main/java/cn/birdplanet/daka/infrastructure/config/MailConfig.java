///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.config;
//
//import java.util.Map;
//import java.util.Properties;
//import javax.mail.Session;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.mail.MailProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//@Slf4j
//@Configuration
//@AutoConfigureAfter
//@EnableConfigurationProperties(MailProperties.class)
//public class MailConfig {
//
//  private final MailProperties properties;
//
//  private final Session session;
//
//  public MailConfig(MailProperties properties, ObjectProvider<Session> session) {
//    this.properties = properties;
//    this.session = session.getIfAvailable();
//  }
//
//  private void applyProperties(JavaMailSenderImpl sender) {
//    sender.setHost(this.properties.getHost());
//    if (this.properties.getPort() != null) {
//      sender.setPort(this.properties.getPort());
//    }
//    sender.setUsername(this.properties.getUsername());
//    sender.setPassword(this.properties.getPassword());
//    sender.setProtocol(this.properties.getProtocol());
//    if (this.properties.getDefaultEncoding() != null) {
//      sender.setDefaultEncoding(this.properties.getDefaultEncoding().name());
//    }
//    if (!this.properties.getProperties().isEmpty()) {
//      sender.setJavaMailProperties(this.asProperties(this.properties.getProperties()));
//    }
//  }
//
//  private Properties asProperties(Map<String, String> source) {
//    Properties properties = new Properties();
//    properties.putAll(source);
//    return properties;
//  }
//
//  @Primary
//  @Bean(name = "noreplyMailSender")
//  public JavaMailSenderImpl noreplyMailSender() {
//    log.info("设置通知邮箱");
//    JavaMailSenderImpl sender = new JavaMailSenderImpl();
//    if (this.session != null) {
//      sender.setSession(this.session);
//    } else {
//      this.applyProperties(sender);
//    }
//    return sender;
//  }
//}
