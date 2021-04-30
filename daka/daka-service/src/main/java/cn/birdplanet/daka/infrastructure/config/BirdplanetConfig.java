/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dwy
 */
@Data
public class BirdplanetConfig {

  private static final Logger log = LoggerFactory.getLogger(BirdplanetConfig.class);

  public static String pathFileUpload;
  public static String secretJwt;
  public static String secretJwtConsole;

  public static String env;
  public static String moneyqrPathPrefix;

  static {
    log.info("初始化「{}」配置文件...", "config.properties");
    Properties prop = new Properties();
    InputStream in =
        BirdplanetConfig.class.getClassLoader().getResourceAsStream("config/config.properties");
    try {
      prop.load(in);
      secretJwt = prop.getProperty("secret_jwt").trim();
      secretJwtConsole = prop.getProperty("secret_jwt").trim();
      env = prop.getProperty("env").trim();
      pathFileUpload = prop.getProperty("path_file_upload").trim();
      moneyqrPathPrefix = prop.getProperty("moneyqr_path_prefix").trim();
    } catch (IOException e) {
      log.error("初始化「{}」配置文件... Error", "config.properties");
      System.exit(1);
    }
  }
}
