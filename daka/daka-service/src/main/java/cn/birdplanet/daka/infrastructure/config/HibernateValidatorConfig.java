///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.config;
//
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import org.hibernate.validator.HibernateValidator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
//
//@Configuration
//public class HibernateValidatorConfig {
//
//  /**
//   * Validator有以下两种验证模式：
//   * <p>
//   * 1、普通模式（默认） (会校验完所有的属性，然后返回所有的验证失败信息)
//   * <p>
//   * 2、快速失败返回模式 快速失败返回模式(只要有一个验证失败，则返回)
//   *
//   * @return javax.validation.Validator
//   */
//  @Bean
//  public Validator validator() {
//    ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
//        .configure()
//        .failFast(true)
//        .buildValidatorFactory();
//
//    Validator validator = validatorFactory.getValidator();
//    return validator;
//  }
//
//  /**
//   * 对RequestParam对应的参数进行注解，需要使用@Validated注解来使得验证生效
//   */
//  @Bean
//  public MethodValidationPostProcessor methodValidationPostProcessor() {
//    MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
//    //设置validator模式为快速失败返回
//    postProcessor.setValidator(validator());
//    return postProcessor;
//  }
//}
