/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch.oauth;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.BaseInfoVO;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.commons.util.JwtTokenUtils;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.pay.IAlipayService;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import cn.birdplanet.toolkit.extra.code.AlipayOAuthScopeCodes;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.UserStatusCodes;
import cn.birdplanet.toolkit.extra.code.UserTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import cn.birdplanet.toolkit.json.JacksonUtil;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "支付宝授权 :: 相关操作")
@RestController
@RequestMapping("punch/oauth/alipay")
public class AlipayOAuthController extends BaseController {

  @Autowired private IAlipayService alipayService;
  @Autowired private IUserService userService;

  public static final String TEST_ALIPAY_UID = "0000000000000000";

  @ApiOperation(value = "app授权", notes = " ")
  @PostMapping("getOAuthInfo")
  public RespDto buildAppOAuthInfo(HttpServletRequest request,
      @RequestParam(required = false, defaultValue = "") String appId,
      @RequestParam(required = false, defaultValue = "") String targetId) {

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("authInfo", alipayService.getAppAuthInfo(appId, targetId));
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "主动授权:需要用户手动点击同意", notes = " ")
  @PostMapping("")
  public RespDto auth(HttpServletRequest request,
      @RequestParam(required = false, defaultValue = "") String appId) {
    String sessionId = request.getSession().getId();
    String key = RedisConstants.ALIPAY_OAUTH_PREFIX + sessionId;
    redisUtils.set(key, sessionId, 10, TimeUnit.MINUTES);
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    dataMap.put("url", alipayService.oauthUser(sessionId, appId));
    String token = JwtTokenUtils.build(sessionId, "授权票据", 10 * 1000 * 60);
    dataMap.put("oauth_token", token);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "静默授权", notes = " ")
  @PostMapping("auth_base")
  public RespDto authBase(HttpServletRequest request,
      @RequestParam(required = false, defaultValue = "") String appId) {
    // 静默授权后，获取用户的userid， 去数据库里查询， 如果不存在此用户， 则需用户主动授权来获取用户信息
    String sessionId = request.getSession().getId();
    String key = RedisConstants.ALIPAY_OAUTH_PREFIX + sessionId;
    redisUtils.set(key, sessionId, 10, TimeUnit.MINUTES);
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    dataMap.put("url", alipayService.oauthBase(sessionId, appId));
    String token = JwtTokenUtils.build(sessionId, "授权票据", 10 * 1000 * 60);
    dataMap.put("oauth_token", token);
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "获取用户信息", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "auth_code", dataType = "String", value = "auth_code"),
  })
  @PostMapping("userinfo")
  public RespDto authUserInfo(@RequestParam String auth_code,
      @RequestParam(required = false, defaultValue = "") String appId,
      @RequestParam(required = false, defaultValue = "") String scope,
      @RequestParam(required = false, defaultValue = "") String state,
      @RequestParam(required = false, defaultValue = "") String useApp) {
    // 获取基本的数据信息 qq，wx
    BaseInfoVO baseInfoVO = userService.getBaseInfo();

    // 测试账号专用
    RespDto respDtoForTest = this.getRespDtoForTest(auth_code, baseInfoVO);
    if (null != respDtoForTest) {
      return respDtoForTest;
    }
    String openUid = "";
    AlipaySystemOauthTokenResponse oauthToken;
    //
    try {
      oauthToken = alipayService.getAccessToken(appId, scope, auth_code, "authorization_code");
      log.debug("auth_code :: {}, oauthToken :: {}", auth_code, JacksonUtil.obj2Json(oauthToken));
      if (!oauthToken.isSuccess()) {
        log.error("授权失败 auth_code:: {}", auth_code);
        return RespDto.error(ErrorCodes.alipay_sdk_err);
      }
      if (StringUtils.isBlank(oauthToken.getUserId())) {
        return RespDto.error(ErrorCodes.alipay_sdk_err);
      }
      openUid = oauthToken.getUserId();
    } catch (Exception e) {
      log.error("获取AlipaySystemOauthTokenResponse异常：{} >>{}", auth_code, e);
      return RespDto.error(ErrorCodes.alipay_sdk_err);
    }
    // 查询缓存里的数据：数据存在延迟
    User user = userService.getByOpenUid(openUid, UserTypeCodes.alipay);
    if (null == user) {
      // 如果是静默授权且用户不存在的话，需要用户点击授权
      if (AlipayOAuthScopeCodes.AUTH_BASE.getCode().equalsIgnoreCase(scope)) {
        Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
        dataMap.put("userinfo", null);
        return RespDto.succData(dataMap);
      }
      AlipayUserInfoShareResponse alipayUserInfo =
          alipayService.getUserInfo(oauthToken.getAccessToken());
      if (!alipayUserInfo.isSuccess()) {
        return RespDto.error(ErrorCodes.alipay_sdk_err);
      }
      log.debug("支付宝用户[{}]不存在，保存新用户", oauthToken.getUserId());
      log.debug("AliUserId :: {}, alipayUserInfo :: {}", oauthToken.getUserId(),
          JacksonUtil.obj2Json(alipayUserInfo));
      user = this.buildUserWithAli(alipayUserInfo);
      user.setInvitationCode(userService.generateInviteCode());
      user.setCreatedAt(LocalDateTime.now());
      user.setCreatedDate(user.getCreatedAt().toLocalDate());
      user.setIsFirstWithdraw(YesOrNoCodes.YES.getCode());
      userService.save(user);
      log.debug("支付宝用户[{}]，用户信息:: {}", oauthToken.getUserId(), user);
    }
    // 根据用户的
    user = userService.getByUidFromRedis(user.getUid());
    // 封锁账号不能参加
    if (UserStatusCodes.closed.getCode() == user.getStatus()) {
      return RespDto.error(ErrorCodes.account_lock_error);
    }
    UserDtlVO currUserDtlVo = DozerMapperUtil.map(user, UserDtlVO.class);
    String key = RedisConstants.USER_LOGIN_KEY_PREFIX + currUserDtlVo.getUid();
    // 有效期
    long readmeTimeTtl = (1000 * 60 * 60 * 24L) * (StringUtils.isNotBlank(useApp) ? 30 : 7);
    //
    redisUtils.set(key, currUserDtlVo, readmeTimeTtl, TimeUnit.MILLISECONDS);

    String token =
        JwtTokenUtils.build(currUserDtlVo.getUid(), currUserDtlVo.getUserType(), readmeTimeTtl);
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
    dataMap.put("token", token);
    dataMap.put("userinfo", currUserDtlVo);
    dataMap.put("baseinfo", baseInfoVO);
    return RespDto.succData(dataMap);
  }

  private User buildUserWithAli(AlipayUserInfoShareResponse response) {
    User user = new User();
    user.setUserType(UserTypeCodes.alipay.getCode());
    user.setOpenUid(response.getUserId());
    user.setAliUserAccount("");
    user.setStatus(UserStatusCodes.normal.getCode());
    user.setNickName(response.getNickName());
    user.setAvatarPath(response.getAvatar());
    user.setMobile(response.getMobile());
    user.setGender(User.convertGenderByAlipay(response.getGender()));
    user.setIsCertified(User.convertIsCertifiedByAlipay(response.getIsCertified()));
    user.setBrokerageWithdrawalSwitch(YesOrNoCodes.NO.getCode());
    return user;
  }

  private RespDto getRespDtoForTest(String auth_code, BaseInfoVO baseInfoVO) {
    // todo 特殊情况：支付宝审核使用
    if (TEST_ALIPAY_UID.startsWith(auth_code)) {
      User user = userService.getByOpenUid(auth_code, UserTypeCodes.alipay);
      UserDtlVO currUserDtlVo = DozerMapperUtil.map(user, UserDtlVO.class);
      String key = RedisConstants.USER_LOGIN_KEY_PREFIX + currUserDtlVo.getUid();
      long readmeTimeTtl = 1000 * 60 * 60 * 24L;
      redisUtils.set(key, currUserDtlVo, readmeTimeTtl, TimeUnit.MILLISECONDS);
      String token =
          JwtTokenUtils.build(currUserDtlVo.getUid(), currUserDtlVo.getUserType(), readmeTimeTtl);
      Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
      dataMap.put("token", token);
      dataMap.put("userinfo", currUserDtlVo);
      dataMap.put("baseinfo", baseInfoVO);
      return RespDto.succData(dataMap);
    }
    return null;
  }
}
