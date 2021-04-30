/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: MainActivityController
 * @date 2019-08-13 19:23
 */
@Api(tags = "房间模式 :: 相关操作")
@Slf4j
@RequestMapping("console/room-model")
@RestController("consoleRoomModelController")
public class RoomModelController extends BaseController {

}
