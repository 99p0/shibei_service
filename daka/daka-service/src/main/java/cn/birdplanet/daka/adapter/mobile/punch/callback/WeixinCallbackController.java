/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch.callback;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "微信回调通知 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/callback/weixin")
public class WeixinCallbackController {

  @ApiOperation(value = "异步通知", notes = "Notes")
  @PostMapping("notify")
  public String wx_notify() {

    return "SUCCESS";
  }
}
