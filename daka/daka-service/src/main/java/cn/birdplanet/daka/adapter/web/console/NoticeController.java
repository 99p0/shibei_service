/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
 * @title: PunchController
 * @date 2019-07-08 09:36
 */
@Api(tags = "系统通知 :: 相关操作")
@Slf4j
@RequestMapping("console/notice")
@RestController("consoleNoticeController")
public class NoticeController extends BaseController {

  @Autowired private INoticeService noticeService;

  @ApiOperation(value = "系统通知", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("list")
  public RespDto getNoticeByPage(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List list = noticeService.getAllWithPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(list);
    pageInfo.setList(list);
    return RespDto.succData(pageInfo);
  }
  @ApiOperation(value = "查询个人系统通知", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("record")
  public RespDto getRecordByUidWithPage(@RequestParam long uid,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List list = noticeService.getByUidWithPage(pageNum, pageSize, uid);
    PageInfo pageInfo = PageInfo.of(list);
    pageInfo.setList(list);
    return RespDto.succData(pageInfo);
  }

}
