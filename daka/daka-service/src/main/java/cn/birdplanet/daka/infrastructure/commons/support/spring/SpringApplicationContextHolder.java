/*
 * Copyright (c) 海绵保 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.commons.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created on 16/7/2.
 */
@Component
public class SpringApplicationContextHolder implements ApplicationContextAware {

  public static ApplicationContext ctx;

  public static Object getBean(Class classes) {
    return ctx.getBean(classes);
  }

  public static Object getBean(Class classes, String clazzName) {
    return ctx.getBean(clazzName, classes);
  }

  @Override public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    SpringApplicationContextHolder.ctx = ctx;
  }
}
