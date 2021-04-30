/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.NormalModeOrderDTO;
import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.INormalModeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
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
 */
@Api(tags = "打卡*常规模式 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/normal-mode")
public class NormalModeController extends BaseController {

  @Autowired private IUserService userService;
  @Autowired private INormalModeService normalModeService;

  @ApiOperation(value = "活动广场", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("activities/plaza")
  public RespDto getActivitiesForPlaza(@RequestAttribute UserDtlVO currUserDtlVo) {
    PageInfo pageInfo = normalModeService.getPlazaActivities();
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "已报名的活动", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("activities/registered")
  public RespDto getActivitiesForRegistering(@RequestAttribute UserDtlVO currUserDtlVo) {
    List<NormalModeOrder> orders = normalModeService.getOrdersForRegistered(currUserDtlVo.getUid());
    List<NormalModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
    orders.forEach(order -> {
      NormalModeOrderDTO dto = new NormalModeOrderDTO(order);
      NormalMode activity = normalModeService.getActivityByIdFromRedis(order.getActivityId());
      // 需要用转换后的数据
      dto.setNormalModeDTO(normalModeService.normalMode2Dto(activity));
      // 打卡记录
      dto.setRounds(normalModeService.getRounds(order.getId(), order.getUid()));
      dtos.add(dto);
    });
    PageInfo pageInfo = new PageInfo(orders);
    pageInfo.setList(dtos);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "打卡中的活动", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("orders/punching")
  public RespDto getActivitiesForPunching(@RequestAttribute UserDtlVO currUserDtlVo) {
    List<NormalModeOrder> orders = normalModeService.getOrdersForPunching(currUserDtlVo.getUid());
    List<NormalModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
    orders.forEach(order -> {
      NormalModeOrderDTO dto = new NormalModeOrderDTO(order);
      NormalMode activity = normalModeService.getActivityByIdFromRedis(order.getActivityId());
      // 需要用转换后的数据
      dto.setNormalModeDTO(normalModeService.normalMode2Dto(activity));
      // 打卡记录
      dto.setRounds(normalModeService.getRounds(order.getId(), order.getUid()));
      dtos.add(dto);
    });
    PageInfo pageInfo = new PageInfo(orders);
    pageInfo.setList(dtos);
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "参加过的活动", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", dataType = "int", name = "status", value = "是否成功:0全部1成功2失败"),
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("orders/joined")
  public RespDto getActivitiesForPunching(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "0") int status,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "15") int pageSize) {
    List<NormalModeOrder> orders =
        normalModeService.getOrdersForJoinedWithPage(currUserDtlVo.getUid(), status, pageNum,
            pageSize);
    PageInfo pageInfo = new PageInfo(orders);
    if (!orders.isEmpty()) {
      List<NormalModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
      orders.forEach(order -> {
        // todo 此处内容 更改为缓存
        NormalModeOrderDTO dto = new NormalModeOrderDTO(order);
        NormalMode activity = normalModeService.getActivityByIdFromRedis(order.getActivityId());
        // 需要用转换后的数据
        dto.setNormalModeDTO(normalModeService.normalMode2Dto(activity));
        dto.setRounds(normalModeService.getRounds(order.getId(), order.getUid()));
        dtos.add(dto);
      });
      // 具体的内容
      pageInfo.setList(dtos);
    }
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "获取某期打卡的状态", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "id", dataType = "int", value = "编码"),
      @ApiImplicitParam(paramType = "query", name = "period", dataType = "int", value = "周期"),
  })
  @PostMapping("activity/status")
  public RespDto status(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long id, @RequestParam String period) {
    HashMap dataMap = Maps.newHashMapWithExpectedSize(5);
    // 活动信息
    NormalMode activity = normalModeService.getActivityByIdFromRedis(id);
    // 用户参加的信息
    NormalModeOrder order = normalModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
    // 只有在打卡中的状态，检查,且活动已经开始
    if (null != order && order.getStatus() == 1
        && activity.getStartDatetime().isBefore(LocalDateTime.now())) {
      if (normalModeService.checkInPunchingTime(order)) {
        // 如果操作过的话， 需要重新获取订单信息
        order = normalModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
        activity = normalModeService.getActivityByIdFromRedis(activity.getId());
      }
    }
    dataMap.put("activity", normalModeService.normalMode2Dto(activity));
    dataMap.put("order", order);
    dataMap.put("joined", null != order);
    //打卡记录
    dataMap.put("rounds",
        null != order ? normalModeService.getRounds(order.getId(), order.getUid()) : null);
    // 参加的活动列表信息 :: 目前暂不显示
    //String rkey = normalModeService.getRKeyForJoinedUsers(id);
    //dataMap.put("joinedUList", normalModeService.getJoinedUsersByActivityId(id));
    //dataMap.put("joinedUSize", redisUtils.hash_len(rkey));
    dataMap.put("joinedUList", null);
    dataMap.put("joinedUSize", 0);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "报名参加打卡", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "id", dataType = "int", value = "编码"),
      @ApiImplicitParam(paramType = "query", name = "period", dataType = "int", value = "周期"),
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "选择的金额"),
      @ApiImplicitParam(paramType = "query", name = "multiple", dataType = "int", value = "选择的倍数"),
  })
  @PostMapping("join")
  public RespDto join(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long id, @RequestParam String period,
      @RequestParam BigDecimal amount, @RequestParam int multiple) {
    HashMap dataMap = Maps.newHashMapWithExpectedSize(2);
    NormalMode activity = normalModeService.getActivityByIdFromRedis(id);
    // 挑战金, 倍数
    BigDecimal challengeAmount = amount.divide(new BigDecimal(multiple), 3, BigDecimal.ROUND_DOWN);
    if (activity.getChallengeAmount().compareTo(challengeAmount) != 0) {
      dataMap.put("flag", "errAmount");
      dataMap.put("msg", "无效挑战金额");
      return RespDto.succData(dataMap);
    }
    LocalDateTime nowLdt = LocalDateTime.now();
    // 活动截止
    if (nowLdt.isAfter(activity.getStartDatetime())) {
      dataMap.put("flag", "stop");
      dataMap.put("msg", "此活动报名已结束");
      return RespDto.succData(dataMap);
    }
    //
    User user = userService.getByUid(currUserDtlVo.getUid());
    log.debug("join >>> user.balance={}, amount={}, multiple={}", user.getBalance(), amount,
        multiple);
    if (user.getBalance().compareTo(amount) == -1) {
      dataMap.put("flag", "not_enough");
      dataMap.put("msg", ErrorCodes.balance_not_enough.getDesc());
      return RespDto.succData(dataMap);
    }
    NormalModeOrder order = normalModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
    if (null != order) {
      dataMap.put("flag", "joined");
      dataMap.put("msg", "已经预约打卡");
      return RespDto.succData(dataMap);
    }
    //
    boolean flag = normalModeService.join(activity, user, id, period, amount, multiple);
    dataMap.put("flag", "" + flag);
    if (!flag) {
      dataMap.put("msg", ErrorCodes.err.getDesc());
    }
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "签到", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "int", value = "编码"),
      @ApiImplicitParam(paramType = "query", name = "period", dataType = "int", value = "周期"),
  })
  @PostMapping("checkin")
  public RespDto checkin(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long aid, @RequestParam long oid) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    NormalMode activity = normalModeService.getActivityByIdFromRedis(aid);
    // 活动尚未开始
    if (activity.getStartDatetime().isAfter(LocalDateTime.now())) {
      dataMap.put("flag", "notStart");
      dataMap.put("msg", "活动尚未开始");
      return RespDto.succData(dataMap);
    }
    // 是否预约了此活动
    NormalModeOrder order = normalModeService.getOrderById(oid, currUserDtlVo.getUid());
    if (null == order) {
      dataMap.put("flag", "notJoined");
      dataMap.put("msg", "尚未预约此活动");
      return RespDto.succData(dataMap);
    }
    String flag = normalModeService.checkin(currUserDtlVo.getUid(), order, LocalDateTime.now());
    dataMap.put("flag", flag);
    if ("notAt".equalsIgnoreCase(flag)) {
      dataMap.put("msg", "请于规定时间内签到");
    }
    return RespDto.succData(dataMap);
  }
}
