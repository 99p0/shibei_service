/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
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
 * @title: WalletController
 * @date 2019-07-08 09:36
 */
@Api(tags = "钱包 :: 相关操作")
@Slf4j
@RestController("consoleWalletController")
@RequestMapping("console/wallet")
public class WalletController extends BaseController {

  @Autowired private IUserService userService;
  @Autowired private IWalletService walletService;

  @ApiOperation(value = "活动列表", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/list")
  public RespDto getAllRecord(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<WalletDtl> gameModeList = walletService.getAllByPage(pageNum, pageSize);
    PageInfo pageInfo = PageInfo.of(gameModeList);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "某个人的钱包明细", notes = "Notes")
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

    List<WalletDtl> list =
        walletService.getWalletDtlByPage(pageNum, pageSize, uid);
    PageInfo pageInfo = new PageInfo(list);
    pageInfo.setList(list);
    return RespDto.succData(pageInfo);
  }
}
