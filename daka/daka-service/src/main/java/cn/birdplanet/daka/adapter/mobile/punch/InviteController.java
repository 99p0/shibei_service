/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.ServiceFuwu;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.AlipayContactVO;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.service.IInviteService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.FuwuTypeCodes;
import cn.birdplanet.toolkit.json.JacksonUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Api(tags = "邀请 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/invite")
public class InviteController {

  @Autowired private IInviteService inviteService;
  @Autowired private IUserService userService;

  @ApiOperation(value = "不在弹出输入邀请码", notes = "Notes")
  @PostMapping("not_alert")
  public RespDto notAlert(@RequestAttribute UserDtlVO currUserDtlVo) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(1);
    boolean flag = userService.updateNotAlertInviteView(currUserDtlVo.getUid(), "N");
    dataMap.put("flag", flag);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "输入邀请码", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "code", dataType = "String", value = "邀请码"),
  })
  @PostMapping("code")
  public RespDto inputInviterCode(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam String code) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    if (StringUtils.isBlank(code)) {
      dataMap.put("flag", false);
      dataMap.put("subMsg", "邀请码不能为空");
      return RespDto.succData(dataMap);
    }
    // 不能邀请自己
    if (currUserDtlVo.getInvitationCode().equalsIgnoreCase(code)) {
      return RespDto.error(ErrorCodes.invite_myself);
    }
    // 邀请码是否存在
    User inviter = userService.getByInvitationCode(code);
    if (null == inviter) {
      return RespDto.error(ErrorCodes.invite_code_not_exist);
    }
    // 如果是自己的上家也不行 》》》例如：我填写你的， 你填写我的
    if (StringUtils.isNotBlank(currUserDtlVo.getInviterCode())
        && currUserDtlVo.getInviterCode().equalsIgnoreCase(code)) {
      return RespDto.error(ErrorCodes.invite_ex_exist);
    }
    boolean flag = inviteService.inputInviterCode(currUserDtlVo, code, inviter);
    // 更新成功后， 更新用户redis中的缓存信息
    if (flag) {

    }
    dataMap.put("flag", flag);
    dataMap.put("subMsg", flag ? "" : "ERROR，请稍后再试");
    return RespDto.succData(dataMap);
  }

  @Deprecated
  @ApiOperation(value = "发送邀请消息", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "contacts", dataType = "String", value = "json"),
  })
  @PostMapping("send-invited-msg")
  public RespDto sendInvitedMsg(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam String contacts) {

    //{"contacts":[{"account":"181******96","email":"","mobile":"181******96","userId":"2088532697052687","avatar":"","realName":""}]}
    log.debug("contacts >>> {}", contacts);

    ArrayList<AlipayContactVO> list = JacksonUtil.json2Collection(contacts,
        JacksonUtil.getCollectionType(ArrayList.class, AlipayContactVO.class));

    List<String> aliUids = list.stream().map(vo -> vo.getUserId()).collect(Collectors.toList());
    log.debug("contacts list size>>> {}", list.size());

    inviteService.sendInvitedMessage(currUserDtlVo, aliUids);
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("flag", true);
    dataMap.put("subMsg", "");
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "获取服务号信息", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "type", dataType = "String", value = "json"),
  })
  @PostMapping("fuwu")
  public RespDto getFuwu(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "alipay") String type) {
    FuwuTypeCodes typeCodes = FuwuTypeCodes.codeOf(type);
    if (null == typeCodes) {
      typeCodes = FuwuTypeCodes.ALIPAY;
    }
    ServiceFuwu fuwu = inviteService.getFuwuByType(typeCodes);
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("flag", null != fuwu);
    dataMap.put("data", fuwu);
    return RespDto.succData(dataMap);
  }
}
