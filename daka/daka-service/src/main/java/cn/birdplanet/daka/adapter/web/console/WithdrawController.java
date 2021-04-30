/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.dto.WalletWithdrawAppDTO;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.WalletWithdrawApp;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWithdrawService;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Api(tags = "提现转账 :: 相关操作")
@Slf4j
@RequestMapping("console/wallet/withdraw")
@RestController("consoleWithdrawController")
public class WithdrawController extends BaseController {

  @Autowired private IWithdrawService withdrawService;
  @Autowired private IUserService userService;

  @ApiOperation(value = "申请提现的列表", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping(value = "applist")
  public RespDto applist(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize,
      @RequestParam(required = false, defaultValue = "") String status) throws Exception {

    List<WalletWithdrawApp> appList =
        withdrawService.getWithdrawApplist(pageNum, pageSize,
            StringUtils.isBlank(status) ? null : Integer.parseInt(status));
    List<WalletWithdrawAppDTO> dtos = Lists.newArrayList();
    appList.forEach(app -> {
      WalletWithdrawAppDTO dto = DozerMapperUtil.map(app, WalletWithdrawAppDTO.class);
      dto.setUser(userService.getByUid(app.getUid()));
      dtos.add(dto);
    });
    PageInfo pageInfo = PageInfo.of(appList);
    pageInfo.setList(dtos);
    // 转换数据
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "确认转账", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping(value = "confirm")
  public RespDto confirmTransfer(@RequestAttribute Admin currAdmin,
      @RequestParam long id,
      @RequestParam(required = false, defaultValue = "1") String status,
      @RequestParam(required = false, defaultValue = "") String remark) throws Exception {

    int statusParse = Integer.parseInt(status);
    boolean flag = false;
    if (statusParse == 0 || statusParse == 1) {
      flag = withdrawService.confirmTransfer(id, statusParse, remark);
    }
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(1);
    dataMap.put("flag", flag);
    return RespDto.succData(dataMap);
  }
}
