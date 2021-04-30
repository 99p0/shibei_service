/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2021 All Rights Reserved.
 */
package cn.birdplanet.daka.infrastructure.config;

import cn.birdplanet.daka.infrastructure.commons.util.EnvUtils;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestAttribute;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

  @Bean
  public Docket api_web() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("前端API")
        .select()
        .apis(RequestHandlerSelectors.any())
        //扫描所有有注解的api，用这种方式更灵活
        //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(regex("/punch.*"))
        .build()
        //添加登录认证
        //.securitySchemes(this.securitySchemes())
        //.securityContexts(this.securityContexts())
        //
        .ignoredParameterTypes(RequestAttribute.class)
        .directModelSubstitute(LocalDate.class, String.class)
        .apiInfo(this.apiInfo())
        .enable(!EnvUtils.isRelease());
  }

  @Bean
  public Docket api_console() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("管理端API")
        .select()
        .apis(RequestHandlerSelectors.any())
        //扫描所有有注解的api，用这种方式更灵活
        //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(regex("/console.*"))
        .build()
        //添加登录认证
        //.securitySchemes(this.securitySchemes())
        //.securityContexts(this.securityContexts())
        //
        .ignoredParameterTypes(RequestAttribute.class)
        .directModelSubstitute(LocalDate.class, String.class)
        .apiInfo(this.apiInfo())
        .enable(!EnvUtils.isRelease());
  }

  private ApiInfo apiInfo() {

    List vendors = new ArrayList<VendorExtension>();
    vendors.add(new StringVendorExtension("杨润", "uncle.yang@outlook.com"));
    return
        new ApiInfo("小鸟星球 api wiki",
            "",
            "1.0.0",
            "https://zhuomuniaodaka.com",
            ApiInfo.DEFAULT_CONTACT,
            "Birdplanet Icn.",
            "https://zhuomuniaodaka.com/license.txt",
            vendors
        );
  }

  private List<ApiKey> securitySchemes() {
    //设置请求头信息
    List<ApiKey> result = new ArrayList<>();
    ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
    result.add(apiKey);
    return result;
  }

  private List<SecurityContext> securityContexts() {
    //设置需要登录认证的路径
    List<SecurityContext> result = new ArrayList<>();
    result.add(getContextByPath("/brand/.*"));
    return result;
  }

  private SecurityContext getContextByPath(String pathRegex) {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex(pathRegex))
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    List<SecurityReference> result = new ArrayList<>();
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    result.add(new SecurityReference("Authorization", authorizationScopes));
    return result;
  }
}
