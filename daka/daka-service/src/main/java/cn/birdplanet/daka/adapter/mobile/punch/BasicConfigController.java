/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Api(tags = "基础配置 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/basic/config")
public class BasicConfigController extends BaseController {

  @ApiOperation(value = "获取所有的基础配置", notes = "Notes")
  @ApiImplicitParams({
  })
  @PostMapping("")
  public RespDto all(@RequestAttribute UserDtlVO currUserDtlVo) {

    // qq号
    // 微信号
    // 打卡规则链接： 闯关，常规，房间
    // 邀请规则链接：
    //
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    return RespDto.succData(dataMap);
  }
}
