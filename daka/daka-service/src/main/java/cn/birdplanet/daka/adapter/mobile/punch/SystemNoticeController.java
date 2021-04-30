/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.SysNotice;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.ISysNoticeService;
import cn.birdplanet.toolkit.extra.code.ModeSimpleCodes;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchController
 * @date 2019-07-08 09:36
 */
@Api(tags = "系统通知 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/notice-sys")
public class SystemNoticeController extends BaseController {

  @Autowired private ISysNoticeService sysNoticeService;

  @ApiOperation(value = "闯关模式", notes = "Notes")

  @PostMapping("game")
  public RespDto getSysNotice(@RequestAttribute UserDtlVO currUserDtlVo) {
    List<SysNotice> list = sysNoticeService.getNoticeForGame();
    // 将集合转换成字符
    String msg = list.isEmpty() ? "" : IntStream.range(0, list.size())
        .mapToObj(i -> (i + 1) + "、" + list.get(i).getContent() + "；")
        .collect(Collectors.joining());
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("hasNotice", !list.isEmpty());
    dataMap.put("noticeContent", msg);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "闯关模式", notes = "Notes")
  @ApiImplicitParams({
  })
  @PostMapping("game/type")
  public RespDto getGameSysNoticeWithType(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam String type) {
    List<SysNotice> list = sysNoticeService.getSysNoticeWithType(ModeSimpleCodes.GameMode, type);
    // 将集合转换成字符
    String msg = list.isEmpty() ? "" : IntStream.range(0, list.size())
        .mapToObj(i -> (i + 1) + "、" + list.get(i).getContent() + "；")
        .collect(Collectors.joining());
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("hasNotice", !list.isEmpty());
    dataMap.put("noticeContent", msg);
    return RespDto.succData(dataMap);
  }
}
