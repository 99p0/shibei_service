package cn.birdplanet.schedulerx.config.datasource;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidStatViewConfig {

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    bean.setFilter(new WebStatFilter());
    bean.addUrlPatterns("/*");
    bean.addInitParameter("exclusions",
        "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/public/*,/error/*,/captcha/*,/swagger-resources,/v2/api-docs,/api-docs,/swagger-resources/*,/webjars/*,/index.html");
    return bean;
  }

  @Bean
  public ServletRegistrationBean druidServlet() {
    ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    bean.addInitParameter("loginUsername", "uncle.yang@outlook.com");
    bean.addInitParameter("loginPassword", "birdplanet");
    bean.addInitParameter("sessionStatEnable", "true");
    bean.addInitParameter("resetEnable", "true");
    bean.addInitParameter("profileEnable", "true");
    return bean;
  }
}
