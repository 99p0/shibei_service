/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.commons.util.JwtTokenUtils;
import cn.birdplanet.daka.infrastructure.service.IAdminService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: LoginController
 * @date 2019-08-13 19:23
 */
@Api(tags = "登录 :: 相关操作")
@Slf4j
@RequestMapping("console/login")
@RestController("consoleLoginController")
public class LoginController extends BaseController {

  @Autowired private IAdminService adminService;

  @ApiOperation(value = "登录", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "userName", dataType = "String", value = "用户名"),
      @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", value = "密码"),
      @ApiImplicitParam(paramType = "query", name = "remember", dataType = "String", value = "记住我: true:一个月， false：一天"),
  })
  @PostMapping(value = "")
  public RespDto login(@RequestParam String userName, @RequestParam String password,
      @RequestParam(required = false, defaultValue = "") String remember) {

    long readmeTimeTtl =
        StringUtils.isNotBlank(remember) ? BirdplanetConstants.ONE_DAY_MILLISECONDS * 31
            : BirdplanetConstants.ONE_DAY_MILLISECONDS;

    Admin admin = adminService.getByUserNameAndPwd(userName, password);
    if (null == admin) {
      return RespDto.error(ErrorCodes.account_pwd_error);
    }
    String key = RedisConstants.ADMIN_KEY_PREFIX + admin.getUid();
    redisUtils.set(key, admin, readmeTimeTtl, TimeUnit.MILLISECONDS);

    String token =
        JwtTokenUtils.buildForConsole(admin.getUid(), admin.getAdminType(), readmeTimeTtl);

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("token", token);
    dataMap.put("name", admin.getName());
    return RespDto.succData(dataMap);
  }
}
