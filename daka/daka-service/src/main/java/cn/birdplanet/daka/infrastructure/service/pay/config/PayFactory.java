//package com.birdplanet.service.pay.config;
//
//import cn.birdplanet.toolkit.core.ClassUtils;
//import com.birdplanet.service.pay.config.alipay.IAlipayConfig;
//import com.birdplanet.service.pay.config.weixin.IWxpayConfig;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class PayFactory {
//
//  private static Map<String, String> nameMap;
//  private static Map<String, IPayConfig> alipayMap;
//
//  public PayFactory() {
//    nameMap = new HashMap<>();
//    alipayMap = new HashMap<>();
//    payConfigInit();
//  }
//
//  private void payConfigInit() {
//    log.debug("payConfigInit init ...");
//    // 获取该接口目录下的所有类
//    List<Class<?>> cs = ClassUtils.getAllClassByInterface(IPayConfig.class);
//    cs.forEach(cla -> {
//      if (!cla.isInterface()) {
//        if (cla.isAnnotationPresent(PayConfig.class)) {
//          PayConfig company = cla.getAnnotation(PayConfig.class);
//          String cName = cla.getName();
//          String keyName = company.name().equalsIgnoreCase("") ? StringUtils.uncapitalize(cName)
//              : company.name();
//          try {
//            alipayMap.put(keyName, (IPayConfig) Class.forName(cName).newInstance());
//          } catch (Exception e) {
//            log.error("payConfig IOC初始化失败,类名为 [" + cName + "] 不存在", e);
//          }
//          nameMap.put(keyName, cName);
//        }
//      }
//    });
//  }
//
//  public IAlipayConfig getAlipayConfig(String name) {
//    try {
//      String cName = nameMap.get(name);
//      if (Class.forName(cName).getAnnotation(PayConfig.class).singleton()) {
//        IAlipayConfig resObj = (IAlipayConfig) alipayMap.getOrDefault(name, null);
//        if (resObj == null) {
//          resObj = (IAlipayConfig) Class.forName(cName).newInstance();
//        }
//        alipayMap.put(name, resObj);
//        return resObj;
//      } else {
//        return (IAlipayConfig) Class.forName(cName).newInstance();
//      }
//    } catch (Exception e) {
//      log.error("获取bean 出错:", e);
//      return null;
//    }
//  }
//
//  public IWxpayConfig getWxpayConfig(String name) {
//    try {
//      String cName = nameMap.get(name);
//      if (Class.forName(cName).getAnnotation(PayConfig.class).singleton()) {
//        IWxpayConfig resObj = (IWxpayConfig) alipayMap.getOrDefault(name, null);
//        if (resObj == null) {
//          resObj = (IWxpayConfig) Class.forName(cName).newInstance();
//        }
//        alipayMap.put(name, resObj);
//        return resObj;
//      } else {
//        return (IWxpayConfig) Class.forName(cName).newInstance();
//      }
//    } catch (Exception e) {
//      log.error("获取bean 出错:", e);
//      return null;
//    }
//  }
//}
