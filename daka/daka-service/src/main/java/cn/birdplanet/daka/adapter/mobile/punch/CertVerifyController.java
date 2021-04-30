/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: CertVerifyController
 * @date 2019-08-13 19:23
 */
@Api(tags = "用户实名管理 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/user/cert/verify")
public class CertVerifyController extends BaseController {

  @Autowired private IUserService userService;

  @ApiOperation(value = "认证", notes = " ")
  @PostMapping(value = "")
  public RespDto getInviter(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "user_name", defaultValue = "") String user_name,
      @RequestParam(name = "cert_no", defaultValue = "") String cert_no) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "输入认证信息", notes = " ")
  @PostMapping(value = "step1")
  public RespDto getVerifyId(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(name = "user_name", defaultValue = "") String user_name,
      @RequestParam(name = "cert_no", defaultValue = "") String cert_no) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "根据认证ID获取认证信息", notes = " ")
  @PostMapping(value = "step2")
  public RespDto verifyById(@RequestAttribute UserDtlVO currUserDtlVo) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    return RespDto.succData(dataMap);
  }
}
