/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.dto.RoomModeOrderDTO;
import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.domain.po.RoomModeOrder;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IRoomModeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 */
@Api(tags = "打卡*房间模式 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/room-mode")
public class RoomModeController extends BaseController {

  @Autowired private IUserService userService;
  @Autowired private IRoomModeService roomModeService;

  @ApiOperation(value = "活动广场", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("activities/plaza")
  public RespDto getActivitiesForPlaza(@RequestAttribute UserDtlVO currUserDtlVo) {
    PageInfo pageInfo = roomModeService.getPlazaActivities();
    return RespDto.succData(pageInfo);
  }

  @ApiOperation(value = "已报名的活动", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "页码"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "每页数量"),
  })
  @PostMapping("activities/registered")
  public RespDto getActivitiesForRegistering(@RequestAttribute UserDtlVO currUserDtlVo) {
    List<RoomModeOrder> orders = roomModeService.getOrdersForRegistered(currUserDtlVo.getUid());
    List<RoomModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
    orders.forEach(order -> {
      RoomModeOrderDTO dto = new RoomModeOrderDTO(order);
      RoomMode activity = roomModeService.getActivityByIdFromRedis(order.getActivityId());
      // 需要用转换后的数据
      dto.setRoomModeDTO(roomModeService.roomMode2Dto(activity));
      // 打卡记录
      dto.setRounds(roomModeService.getRounds(order.getId(), order.getUid()));
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
    List<RoomModeOrder> orders = roomModeService.getOrdersForPunching(currUserDtlVo.getUid());
    List<RoomModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
    orders.forEach(order -> {
      RoomModeOrderDTO dto = new RoomModeOrderDTO(order);
      RoomMode activity = roomModeService.getActivityByIdFromRedis(order.getActivityId());
      // 需要用转换后的数据
      dto.setRoomModeDTO(roomModeService.roomMode2Dto(activity));
      // 打卡记录
      dto.setRounds(roomModeService.getRounds(order.getId(), order.getUid()));
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
    List<RoomModeOrder> orders =
        roomModeService.getOrdersForJoinedWithPage(currUserDtlVo.getUid(), status, pageNum,
            pageSize);
    PageInfo pageInfo = new PageInfo(orders);
    if (!orders.isEmpty()) {
      List<RoomModeOrderDTO> dtos = Lists.newArrayListWithExpectedSize(orders.size());
      orders.forEach(order -> {
        // todo 此处内容 更改为缓存
        RoomModeOrderDTO dto = new RoomModeOrderDTO(order);
        RoomMode activity = roomModeService.getActivityByIdFromRedis(order.getActivityId());
        // 需要用转换后的数据
        dto.setRoomModeDTO(roomModeService.roomMode2Dto(activity));
        dto.setRounds(roomModeService.getRounds(order.getId(), order.getUid()));
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
    RoomMode activity = roomModeService.getActivityByIdFromRedis(id);
    dataMap.put("activity", roomModeService.roomMode2Dto(activity));
    // 用户参加的信息
    RoomModeOrder order = roomModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
    // 只有在打卡中的状态，检查,且活动已经开始
    if (null != order && order.getStatus() == 1
        && activity.getStartDatetime().isBefore(LocalDateTime.now())) {
      if (roomModeService.checkInPunchingTime(order)) {
        // 如果操作过的话， 需要重新获取订单信息
        order = roomModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
      }
    }
    dataMap.put("order", order);
    dataMap.put("joined", null != order);
    //打卡记录
    dataMap.put("rounds",
        null != order ? roomModeService.getRounds(order.getId(), order.getUid()) : null);
    // 参加的活动列表信息
    String rkey = roomModeService.getRKeyForJoinedUsers(id);
    dataMap.put("joinedUList", roomModeService.getJoinedUsersByActivityId(id));
    dataMap.put("joinedUSize", redisUtils.hash_len(rkey));
    //dataMap.put("joinedUList", null);
    //dataMap.put("joinedUSize", 0);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "报名参加打卡", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "id", dataType = "int", value = "编码"),
      @ApiImplicitParam(paramType = "query", name = "period", dataType = "int", value = "周期"),
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "金额"),
      @ApiImplicitParam(paramType = "query", name = "multiple", dataType = "int", value = "倍数"),
  })
  @PostMapping("join")
  public RespDto join(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long id, @RequestParam String period,
      @RequestParam BigDecimal amount, @RequestParam int multiple) {
    // 挑战金* 倍数
    BigDecimal totalAmount = amount.multiply(new BigDecimal(multiple));
    HashMap dataMap = Maps.newHashMapWithExpectedSize(2);

    RoomMode activity = roomModeService.getActivityById(id);
    LocalDateTime nowLdt = LocalDateTime.now();
    // 活动截止
    if (nowLdt.isAfter(activity.getStartDatetime())) {
      dataMap.put("flag", "stop");
      dataMap.put("msg", "此活动报名已结束");
      return RespDto.succData(dataMap);
    }
    // 房主不能参加
    if (currUserDtlVo.getUid().longValue() == activity.getOwnerUid().longValue()) {
      dataMap.put("flag", "false");
      dataMap.put("msg", "不能参加自己的房间");
      return RespDto.succData(dataMap);
    }
    // 是否禁止参加
    if (StringUtils.isNotBlank(activity.getBlocklistUids())
        && activity.getBlocklistUids().contains("," + currUserDtlVo.getUid().toString() + ",")) {
      dataMap.put("flag", "false");
      dataMap.put("msg", "无参加权限");
      return RespDto.succData(dataMap);
    }
    // 是否指定人群打卡的项目
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsOnlySpecial())) {
      // 不存在，则不允许参加
      if (StringUtils.isBlank(activity.getSpecialUids())
          || !activity.getSpecialUids().contains("," + currUserDtlVo.getUid().toString() + ",")) {
        dataMap.put("flag", "false");
        dataMap.put("msg", "暂无参加权限");
        return RespDto.succData(dataMap);
      }
    }
    //
    User user = userService.getByUid(currUserDtlVo.getUid());
    log.debug("join >>> user.balance={}, amount={}", user.getBalance(), totalAmount);
    if (user.getBalance().compareTo(totalAmount) == -1) {
      dataMap.put("flag", "not_enough");
      dataMap.put("msg", ErrorCodes.balance_not_enough.getDesc());
      return RespDto.succData(dataMap);
    }
    RoomModeOrder order = roomModeService.getOrderByActivityId(id, currUserDtlVo.getUid());
    if (null != order) {
      dataMap.put("flag", "joined");
      dataMap.put("msg", "已经预约打卡");
      return RespDto.succData(dataMap);
    }
    //
    boolean flag = roomModeService.join(activity, user, id, period, totalAmount, multiple);
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
    RoomMode activity = roomModeService.getActivityByIdFromRedis(aid);
    // 活动尚未开始
    if (activity.getStartDatetime().isAfter(LocalDateTime.now())) {
      dataMap.put("flag", "notStart");
      dataMap.put("msg", "活动尚未开始");
      return RespDto.succData(dataMap);
    }
    // 是否预约了此活动
    RoomModeOrder order = roomModeService.getOrderById(oid, currUserDtlVo.getUid());
    if (null == order) {
      dataMap.put("flag", "notJoined");
      dataMap.put("msg", "尚未预约此活动");
      return RespDto.succData(dataMap);
    }
    String flag = roomModeService.checkin(currUserDtlVo.getUid(), order, LocalDateTime.now());
    dataMap.put("flag", flag);
    if ("notAt".equalsIgnoreCase(flag)) {
      dataMap.put("msg", "请于规定时间内签到");
    }
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "开房间", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "int", value = "编码"),
      @ApiImplicitParam(paramType = "query", name = "period", dataType = "int", value = "周期"),
  })
  @PostMapping("activity/add")
  public RespDto add(@RequestAttribute UserDtlVO currUserDtlVo) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    return RespDto.succData(dataMap);
  }
}
