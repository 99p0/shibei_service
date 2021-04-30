/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.AppVersion;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IVersionService;
import cn.birdplanet.toolkit.json.jackson.JsonUtils;
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
 * @title: UserController
 * @date 2019-08-13 19:23
 */
@Api(tags = "App版本管理 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/version")
public class VersionController extends BaseController {

  @Autowired private IVersionService versionService;

  @ApiOperation(value = "获取版本信息", notes = " ")
  @PostMapping(value = "new")
  public RespDto getNewVersion(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "android") String os,
      @RequestParam(required = false, defaultValue = "") String appVersion) throws Exception {

    AppVersion currVersion = versionService.getCurrVersion(os);
    Map<String, Object> dataMap =
        null == currVersion ? Maps.newHashMapWithExpectedSize(2) : JsonUtils.toMap(currVersion);
    dataMap.put("vm", currVersion);
    return RespDto.succData(dataMap);
  }
}
