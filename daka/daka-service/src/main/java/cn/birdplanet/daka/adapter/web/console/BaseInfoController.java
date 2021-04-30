/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "app基础信息 :: 相关操作")
@RequestMapping("console/baseinfo")
@RestController("consoleBaseInfoController")
public class BaseInfoController extends BaseController {

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("game_rule/upd")
  public RespDto updateGameRule(@RequestParam(required = false, defaultValue = "") String rule) {
    redisUtils.set1Year(RedisConstants.BASEINFO_GAME_RULE, rule);
    return RespDto.succMsg("OK");
  }

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("game_grid/upd")
  public RespDto updateGameGrid(@RequestParam(required = false, defaultValue = "") String grid) {
    redisUtils.set1Year(RedisConstants.BASEINFO_GAME_GRID, grid);
    return RespDto.succMsg("OK");
  }

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("normal_rule/upd")
  public RespDto updateNormalRule(@RequestParam(required = false, defaultValue = "") String rule) {
    redisUtils.set1Year(RedisConstants.BASEINFO_NORMAL_RULE, rule);
    return RespDto.succMsg("OK");
  }

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("normal_grid/upd")
  public RespDto updateNormalGrid(@RequestParam(required = false, defaultValue = "") String grid) {
    redisUtils.set1Year(RedisConstants.BASEINFO_NORMAL_GRID, grid);
    return RespDto.succMsg("OK");
  }

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("room_rule/upd")
  public RespDto updateRoomRule(@RequestParam(required = false, defaultValue = "") String rule) {
    redisUtils.set1Year(RedisConstants.BASEINFO_ROOM_RULE, rule);
    return RespDto.succMsg("OK");
  }

  @ApiOperation(value = "", notes = "Notes")
  @PostMapping("room_grid/upd")
  public RespDto updateRoomGrid(@RequestParam(required = false, defaultValue = "") String grid) {
    redisUtils.set1Year(RedisConstants.BASEINFO_ROOM_GRID, grid);
    return RespDto.succMsg("OK");
  }
}
