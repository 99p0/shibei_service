/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.service.IRechargeService;
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
 * 邀请
 *
 * @author uncle.yang@outlook.com
 */
@Api(tags = "充值 :: 相关操作")
@Slf4j
@RequestMapping("console/recharge")
@RestController("consoleRechargeController")
public class RechargeController {

  @Autowired private IRechargeService rechargeService;

  @ApiOperation(value = "充值明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/list")
  public RespDto getAllByPage(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List data = rechargeService.getAllWithPage(pageNum, pageSize);
    PageInfo pageInfo = new PageInfo(data);
    pageInfo.setList(data);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "某个人的邀请明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "uid", dataType = "long", value = "用户ID"),
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/record")
  public RespDto getAllByPageAboutWallet(@RequestAttribute Admin currAdmin,
      @RequestParam long uid,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List data = rechargeService.getByUidWithPage(pageNum, pageSize, uid);
    PageInfo pageInfo = new PageInfo(data);
    pageInfo.setList(data);
    return RespDto.succData(pageInfo);
  }
}
