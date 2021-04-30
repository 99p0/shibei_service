/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Program entry
 *
 * @author uncle.yang@outlook.com
 */
@Slf4j
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class DakaApplication {

  public static void main(String[] args) {
    // log4j2 开启 完全异步模式
    System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    SpringApplication app = new SpringApplication(DakaApplication.class);
    // 生成 .pid 文件，里面就会有 PID 。文件的名称和路径。你可以通过 Spring Boot 的配置属性 spring.pid.file 来定制：
    app.addListeners(new ApplicationPidFileWriter());
    app.run(args);
    log.info("拾贝打卡服务 成功启动...");
  }
}
