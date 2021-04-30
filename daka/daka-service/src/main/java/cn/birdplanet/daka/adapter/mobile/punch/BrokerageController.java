/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IBrokerageService;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
@Api(tags = "佣金 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/brokerage")
public class BrokerageController extends BaseController {

  @Autowired
  private IBrokerageService brokerageService;

  @ApiOperation(value = "结算佣金到余额", notes = "Notes")
  @PostMapping("settle")
  public RespDto settle(@RequestAttribute UserDtlVO currUserDtlVo) {
    Map dataMap = Maps.newHashMapWithExpectedSize(2);
    // 每月20号之后方可提取佣金
    LocalDate date = LocalDate.now();
    if (date.getDayOfMonth() < 20) {
      dataMap.put("flag", false);
      dataMap.put("msg", "每月20号之后方可提取佣金");
      return RespDto.succData(dataMap);
    }
    //
    if (YesOrNoCodes.NO.getCode().equalsIgnoreCase(currUserDtlVo.getBrokerageWithdrawalSwitch())) {
      dataMap.put("flag", false);
      dataMap.put("msg", "未满足佣金提取条件");
      return RespDto.succData(dataMap);
    }
    ActionVo actionVo = brokerageService.settle(currUserDtlVo.getUid());
    dataMap.put("flag", actionVo.isAction());
    dataMap.put("msg", actionVo.getMsg());
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "佣金明细", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("dtl/all")
  public RespDto getAllByPage(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List data = brokerageService.getByUidWithPage(pageNum, pageSize, currUserDtlVo.getUid());
    PageInfo pageInfo = new PageInfo(data);
    pageInfo.setList(data);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "已读", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "dtlId", dataType = "long", value = "明细的ID"),
  })
  @PostMapping("dtl/read")
  public RespDto dtlRead(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long dtlId) {
    boolean flag = brokerageService.dtlRead(currUserDtlVo.getUid(), dtlId);
    Map dataMap = Maps.newHashMapWithExpectedSize(1);
    dataMap.put("flag", flag);
    return RespDto.succData(dataMap);
  }
}
