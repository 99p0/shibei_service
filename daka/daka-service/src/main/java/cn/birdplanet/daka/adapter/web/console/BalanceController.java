/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.crypto.BCrypt;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
@Api(tags = "资金明细 :: 相关操作")
@Slf4j
@RestController("consoleBalanceController")
@RequestMapping("console/balance")
public class BalanceController extends BaseController {

  @Autowired private IBalanceService balanceService;
  @Autowired private IUserService userService;

  @ApiOperation(value = "余额明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/list")
  public RespDto getAllByPage(@RequestAttribute Admin currAdmin,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List data = balanceService.getAllWithPage(pageNum, pageSize);
    PageInfo pageInfo = new PageInfo(data);
    pageInfo.setList(data);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "余额明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "uid", dataType = "long", value = "用户ID"),
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/record")
  public RespDto getAllByPageAboutBalance(@RequestAttribute Admin currAdmin,
      @RequestParam long uid,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {

    List<BalanceDtl> list =
        balanceService.getByUidWithPage(pageNum, pageSize, uid);
    PageInfo pageInfo = new PageInfo(list);
    pageInfo.setList(list);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "用户充值", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "uid", dataType = "long", value = "用户UID"),
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "充值金额，证书"),
      @ApiImplicitParam(paramType = "query", name = "payPwd", dataType = "String", value = "支付密码"),
  })
  @PostMapping("recharge")
  public RespDto rechargeByUid(@RequestAttribute Admin currAdmin, @RequestParam long uid,
      @RequestParam int amount, @RequestParam(required = false, defaultValue = "") String payPwd) {

    HashMap<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    // 校验支付密码
    try {
      if (StringUtils.isBlank(payPwd) || payPwd.length() != 6) {
        return RespDto.error(ErrorCodes.pay_pwd_err);
      }
      boolean flag = BCrypt.checkpw(DigestUtils.sha512Hex(payPwd), currAdmin.getPasswordPay());
      if (!flag) {
        return RespDto.error(ErrorCodes.pay_pwd_err);
      }
    } catch (Exception e) {
      return RespDto.error(ErrorCodes.pay_pwd_err);
    }

    // 用户是否存在
    User rechargeUser = userService.getByUid(uid);
    if (null == rechargeUser) {
      return RespDto.error(ErrorCodes.user_not_exists);
    }

    boolean flag = userService.rechargeByUid(uid, amount, payPwd);

    dataMap.put("flag", flag);
    // 转换数据
    return RespDto.succData(dataMap);
  }
}
