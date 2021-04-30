/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IGameModeService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.ArrayList;
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
@Api(tags = "闯关模式 :: 相关操作")
@Slf4j
@RequestMapping("console/game-model")
@RestController("consoleGameModelController")
public class GameModelController extends BaseController {

  @Autowired private IGameModeService gameModeService;

  @ApiOperation(value = "活动列表", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("list")
  public RespDto getAllRecord(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<GameMode> gameModeList = gameModeService.getAllByPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(gameModeList);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "生成今日的任务", notes = "Notes")
  @PostMapping("next")
  public RespDto generateNext() {
    // 结束时间
    GameMode activity = gameModeService.getCurrMainActivity();
    if (null == activity) {
      // 生成今日的任务
      gameModeService.generateNextActivityWithTemplate(LocalDate.now());
      activity = gameModeService.getCurrMainActivity();
    }
    return RespDto.succData(activity);
  }

  @ApiOperation(value = "结算某个周期活动", notes = "Notes")
  @PostMapping("settle")
  public RespDto settleActivity(@RequestParam(value = "period") String periodStr) {
    LocalDate period;
    try {
      period = LocalDate.parse(periodStr);
    } catch (Exception e) {
      log.error("周期格式化失败", e);
      return RespDto.error(ErrorCodes.params_err);
    }
    //
    if (!period.equals(LocalDate.now().minusDays(1))) {
      log.error("目前只能结算上一个周期的活动");
      return RespDto.error(ErrorCodes.params_err);
    }
    log.info("手动结算{}期的闯关... start", period);
    List<GameMode> activities = gameModeService.getActivitiesByPeriod(period);
    List<GameMode> settleActivities = new ArrayList<>();
    if (!activities.isEmpty()) {
      activities.forEach(activity -> {
        if (null != activity
            && activity.getIsAutoSettle().equalsIgnoreCase("N")
            && activity.getIsSettled().equalsIgnoreCase("N")) {
          // 塞进线程池处理， 耗时
          taskExecutor.execute(() -> {
            boolean flag = gameModeService.settle(activity);
            log.info("{}期系统手动结算{}, 结束:: {}", period, flag, activity);
          });

          settleActivities.add(activity);
        } else {
          log.info("{}期手动点击结算 activity:: {}", period, activity);
        }
      });
    }else {
      log.info("{}期不存在", period);
    }
    return RespDto.succData(settleActivities);
  }

  @ApiOperation(value = "发送系统补贴", notes = "Notes")
  @PostMapping("settle-subsidy")
  public RespDto settleSubsidy(@RequestParam(value = "period") String periodStr) {
    LocalDate period;
    try {
      period = LocalDate.parse(periodStr);
    } catch (Exception e) {
      log.error("周期格式化失败", e);
      return RespDto.error(ErrorCodes.params_err);
    }
    //
    if (!period.equals(LocalDate.now().minusDays(1))) {
      log.error("目前只能补贴上一个周期的活动");
      return RespDto.error(ErrorCodes.params_err);
    }
    log.info("发放补贴{}期的闯关... start", period);
    List<GameMode> activities = gameModeService.getActivitiesByPeriod(period);

    if (!activities.isEmpty()) {
      activities.forEach(activity -> {
        // 结算比较耗时
        taskExecutor.execute(() -> {
          boolean flag = gameModeService.settle(activity);
          log.info("{}期发放补贴{}, 结束:: {}", period, flag, activity);
        });
      });
    } else {
      log.info("{}期不存在", period);
    }
    return RespDto.succData(activities);
  }
}
