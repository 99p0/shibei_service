package cn.birdplanet.schedulerx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class SchedulerXApplication {

  public static void main(String[] args) {
    // log4j2 开启 完全异步模式
    System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    SpringApplication app = new SpringApplication(SchedulerXApplication.class);
    // 生成 application.pid 文件，里面就会有 PID 。文件的名称和路径。你可以通过 Spring Boot 的配置属性 spring.pid.file 来定制：
    app.addListeners(new ApplicationPidFileWriter());
    // 屏蔽命令行访问属性
    app.setAddCommandLineProperties(false);
    app.run(args);
    log.info("拾贝打卡 任务模块 > 成功启动...");
  }
}