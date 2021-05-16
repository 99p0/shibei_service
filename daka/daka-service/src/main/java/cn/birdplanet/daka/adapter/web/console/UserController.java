/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
 * @title: WithdrawController
 * @date 2019-08-13 19:23
 */
@Api(tags = "用户管理 :: 相关操作")
@Slf4j
@RequestMapping("console/user")
@RestController("consoleUserController")
public class UserController extends BaseController {

  @Autowired private IUserService userService;

  @ApiOperation(value = "用户列表", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("list")
  public RespDto applist(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List<User> userList = userService.getAllByPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(userList);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "用户信息", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("uid")
  public RespDto getByUid(@RequestAttribute Admin currAdmin, @RequestParam long uid) {
    User userInfo = userService.getByUidFromRedis(uid);
    // 转换数据
    return RespDto.succData(userInfo);
  }

  @ApiOperation(value = "上传用户收款码", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "uid", dataType = "long", value = "用户ID"),
      @ApiImplicitParam(paramType = "query", name = "moneyqr", dataType = "file", value = "收款码文件"),
  })
  @PostMapping("moneyqr/upload")
  public RespDto uploadMoneyQr(@RequestAttribute Admin currAdmin, @RequestParam long uid) {

    // 转换数据
    return RespDto.succData(null);
  }
}
