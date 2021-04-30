/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;

public class MyBatisConfig {

  public static Configuration getMybatisConfig() {
    Configuration mybatisConfig = new Configuration();
    // 全局的映射器启用或禁用缓存
    mybatisConfig.setCacheEnabled(false);
    // 全局启用或禁用延迟加载
    mybatisConfig.setLazyLoadingEnabled(true);
    // 允许或不允许多种结果集从一个单独的语句中返回
    mybatisConfig.setMultipleResultSetsEnabled(true);
    // 使用列标签代替列名
    mybatisConfig.setUseColumnLabel(true);
    mybatisConfig.setDefaultExecutorType(ExecutorType.SIMPLE);
    mybatisConfig.setDefaultStatementTimeout(2500);
    return mybatisConfig;
  }

  public static DruidDataSource getDefaultDruidDataSource() {
    try {
      DruidDataSource dataSource = new DruidDataSource();
      // 配置初始化大小、最小、最大
      dataSource.setInitialSize(5);
      dataSource.setMinIdle(5);
      dataSource.setMaxActive(20);
      // 配置获取连接等待超时的时间
      dataSource.setMaxWait(60000);

      // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -
      dataSource.setTimeBetweenEvictionRunsMillis(60000);
      // 配置一个连接在池中最小生存的时间，单位是毫秒 -
      dataSource.setMinEvictableIdleTimeMillis(300000);
      // 验证连接有效与否的SQL，不同的数据配置不同
      dataSource.setValidationQuery("SELECT 1");
      // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
      dataSource.setTestWhileIdle(true);
      // 这里建议配置为TRUE，防止取到的连接不可用
      dataSource.setTestOnBorrow(true);
      dataSource.setTestOnReturn(true);
      // 打开PSCache，并且指定每个连接上PSCache的大小: 针对oracle
      dataSource.setPoolPreparedStatements(false);
      dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

      // 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
      // 监控统计用的filter:stat 日志用的filter:log4j 防御sql注入的filter:wall
      dataSource.setFilters("stat,wall,slf4j");

      Properties properties = new Properties();
      properties.setProperty("druid.stat.mergeSql", "true");
      properties.setProperty("druid.stat.slowSqlMillis", "5000");

      dataSource.setConnectProperties(properties);
      return dataSource;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
