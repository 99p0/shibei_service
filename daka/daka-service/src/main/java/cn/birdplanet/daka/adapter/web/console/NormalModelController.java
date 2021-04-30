/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.INormalModeService;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: MainActivityController
 * @date 2019-08-13 19:23
 */
@Api(tags = "常规模式 :: 相关操作")
@Slf4j
@RequestMapping("console/normal-model")
@RestController("consoleNormalModelController")
public class NormalModelController extends BaseController {

  @Autowired private INormalModeService normalModeService;

  @ApiOperation(value = "活动列表", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("list")
  public RespDto getAllRecord(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<NormalMode> gameModeList = normalModeService.getAllActivitiesByPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(gameModeList);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "活动模版", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("template")
  public RespDto getAllTemplate(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<NormalModeTemplate> gameModeList =
        normalModeService.getAllTemplateByPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(gameModeList);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "结算本金", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "long", value = "活动ID"),
  })
  @PostMapping("settlePrincipal")
  public RespDto settlePrincipal(@RequestAttribute Admin currAdmin, @RequestParam long aid) {
    NormalMode activity = normalModeService.getActivityById(aid);
    log.info("活动:: {}", activity);
    // 未结算，且此活动的结算方式为自动结算的
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
      return RespDto.succMsg("此常规活动已结算...");
    } else {
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
        long count = normalModeService.settlePrincipalForActivity(activity);
        HashMap<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
        dataMap.put("activity", activity);
        dataMap.put("count", count);
        return RespDto.succData(dataMap);
      } else {
        return RespDto.succMsg("此常规活动为手动结算方式...");
      }
    }
  }

  @ApiOperation(value = "结算奖金", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "long", value = "活动ID"),
  })
  @PostMapping("settleBonus")
  public RespDto settleBonus(@RequestAttribute Admin currAdmin, @RequestParam long aid) {
    LocalDateTime now = LocalDateTime.now();
    NormalMode activity = normalModeService.getActivityById(aid);
    log.info("活动:: {}", activity);
    // 今天开始的任务不算
    LocalDateTime yesterday =
        LocalDateTime.of(now.minusDays(1).toLocalDate(), LocalTime.of(23, 59, 59));
    if (activity.getStartDatetime().isAfter(yesterday)) {
      log.info("{}今天开始的活动不参加奖金分配::{}", activity, yesterday);
      return RespDto.succMsg("今天开始的活动不参加奖金分配...");
    }
    // 未结算，且此活动的结算方式为自动结算的
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettled())) {
      log.info("!!!!! 此常规打卡已结算[{}]", activity);
    } else {
      if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsAutoSettle())) {
        log.info("结算常规奖金任务[{}]。。。start", activity.getId());
        long count = normalModeService.settleBonusForActivity(activity);
        log.info("结算常规奖金任务{}/{}个。。。end", activity.getId(), count);
      } else {
        log.info("### 此活动需要手动结算[{}]", activity);
      }
    }
    return RespDto.succMsg("后台处理中...");
  }
}
