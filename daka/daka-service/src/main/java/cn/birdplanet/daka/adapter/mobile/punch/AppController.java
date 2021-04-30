/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.BaseInfoVO;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: UserController
 * @date 2019-08-13 19:23
 */
@Api(tags = "用户管理 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/app")
public class AppController extends BaseController {

  @Autowired private IUserService userService;

  @ApiOperation(value = "用户信息", notes = " ")
  @PostMapping(value = "info")
  public RespDto getAppNeedInfo(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    // 获取基本的数据信息 qq，wx
    BaseInfoVO baseInfoVO = userService.getBaseInfo();

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
    dataMap.put("userinfo", currUserDtlVo);
    dataMap.put("baseinfo", baseInfoVO);
    dataMap.put("gameUrl", "");
    dataMap.put("normalUrl", "");
    dataMap.put("roomUrl", "");
    return RespDto.succData(dataMap);
  }

}
