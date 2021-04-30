//package cn.birdplanet.config;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// */
//@Configuration
//public class PrometheusMetricsConfig {
//
//  @Bean
//  MeterRegistryCustomizer<MeterRegistry> configurer(
//      @Value("${spring.application.name}") String applicationName) {
//    return registry -> registry.config().commonTags("application", applicationName);
//  }
//}
