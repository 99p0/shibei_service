/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * Druid 链接池的配置
 */
@Configuration
public class PunchDataSourceConfig {

  @Value("${spring.datasource.punch.name}")
  private String name;
  @Value("${spring.datasource.punch.driver-class-name}")
  private String driver_class_name;
  @Value("${spring.datasource.punch.username}")
  private String username;
  @Value("${spring.datasource.punch.password}")
  private String password;
  @Value("${spring.datasource.punch.url}")
  private String url;

  @Primary
  @Bean(name = "punchMapperScannerConfigurer")
  public static MapperScannerConfigurer punchMapperScannerConfigurer() {
    MapperScannerConfigurer msc = new MapperScannerConfigurer();
    msc.setSqlSessionFactoryBeanName("punchSqlSessionFactory");
    msc.setBasePackage("cn.birdplanet.schedulerx.persistence.punch");
    Properties properties = new Properties();
    properties.setProperty("notEmpty", "false");
    properties.setProperty("IDENTITY", "MYSQL");
    msc.setProperties(properties);
    return msc;
  }

  @Primary
  @Bean(name = "punchDataSource")
  public DataSource punchDataSource() {
    DruidDataSource source = MyBatisConfig.getDefaultDruidDataSource();
    source.setName(name);
    source.setDriverClassName(driver_class_name);
    source.setUsername(username);
    source.setPassword(password);
    source.setUrl(url);
    return source;
  }

  @Primary
  @Bean(name = "punchSqlSessionFactory")
  public SqlSessionFactory punchSqlSessionFactory(
      @Qualifier("punchDataSource") DataSource dataSource) throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(dataSource);
    bean.setConfiguration(MyBatisConfig.getMybatisConfig());
    bean.setTypeAliasesPackage("cn.birdplanet.daka.domain.po");
    //分页插件
    Properties properties = new Properties();
    properties.setProperty("helperDialect", "mysql");

    PageInterceptor pageInterceptor = new PageInterceptor();
    pageInterceptor.setProperties(properties);
    //添加插件
    bean.setPlugins(pageInterceptor);
    //添加XML目录
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    bean.setMapperLocations(resolver.getResources("classpath:mapper/punch/*.xml"));
    return bean.getObject();
  }

  /**
   * 配置事物管理器
   */
  @Primary
  @Bean(name = "punchTransactionManager")
  public DataSourceTransactionManager punchTransactionManager(
      @Qualifier("punchDataSource") DataSource dataSource) {
    DataSourceTransactionManager manager = new DataSourceTransactionManager();
    manager.setDataSource(dataSource);
    return manager;
  }
}
