/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch.oauth;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "微信授权 :: 相关操作")
@RestController
@RequestMapping("punch/oauth/weixin")
public class WeixinOAuthController extends BaseController {

  @ApiOperation(value = "获取授权信息", notes = " ")
  @ApiImplicitParams({
  })
  @PostMapping("")
  public RespDto login(@RequestParam String account, @RequestParam String pwd,
      @RequestParam(required = false) String remember, HttpServletRequest request) {
    if (Strings.isNullOrEmpty(account) || Strings.isNullOrEmpty(pwd)) {
      return RespDto.error("-4001", "用户名或密码不能为空");
    }

    return RespDto.succData(null);
  }
}
