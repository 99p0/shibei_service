/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.domain.vo.UserFinancialVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.config.BirdplanetConfig;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.date.DateUtil;
import cn.birdplanet.toolkit.date.support.DateTimeFormatterSupport;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
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
 * @title: UserController
 * @date 2019-08-13 19:23
 */
@Api(tags = "用户管理 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/user")
public class UserController extends BaseController {

  @Autowired private IUserService userService;

  @ApiOperation(value = "邀请人信息", notes = " ")
  @PostMapping(value = "inviter")
  public RespDto getInviter(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    User userInfo = userService.getByUid(currUserDtlVo.getUid());
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("inviterCode", userInfo.getInviterCode());
    dataMap.put("inviter",
        StringUtils.isNotBlank(userInfo.getInviterCode()) ? userService.getByInvitationCode(
            userInfo.getInviterCode()) : "");
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "用户信息", notes = " ")
  @PostMapping(value = "info")
  public RespDto getByUid(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    User userInfo = userService.getByUid(currUserDtlVo.getUid());
    return RespDto.succData(userInfo);
  }

  @ApiOperation(value = "用户金融信息", notes = " ")
  @PostMapping(value = "info/financial")
  public RespDto financialInfo(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    User userInfo = userService.getByUid(currUserDtlVo.getUid());
    UserFinancialVO userFinancialVO = new UserFinancialVO(userInfo);
    String key = RedisConstants.TODAY_WITHDRAW_TIMES_KEY_PREFIX + currUserDtlVo.getUid();
    long expire = redisUtils.getExpire(key);
    if (expire <= 0) {
      redisUtils.del(key);
    }
    Integer times = (Integer) redisUtils.get(key);
    log.debug("{} 提现次数 ::", key, times);
    userFinancialVO.setIsTodayWithdraw(null != times && times.intValue() >= 1);
    return RespDto.succData(userFinancialVO);
  }

  @ApiOperation(value = "上传收款码", notes = " ")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "moneyqr", dataType = "MultipartFile", value = "上传的文件"),
  })
  @PostMapping(value = "upload/moneyqr")
  public RespDto uploadMoneyQr(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam("moneyqr") String moneyqrB64) throws IOException {
    if (StringUtils.isBlank(moneyqrB64)) {
      return RespDto.error(ErrorCodes.uploadmoneyqr_null);
    }
    // 获取文件后缀
    String prefix = moneyqrB64.substring(0, moneyqrB64.indexOf(";"));
    prefix = prefix.substring(prefix.indexOf("/") + 1);
    log.debug("prefix >> {}", prefix);
    String submittedFileName = prefix.toLowerCase();
    List<String> imgSubs = Arrays.asList("png", "jpg", "jpeg");
    if (!imgSubs.contains(submittedFileName)) {
      return RespDto.error(ErrorCodes.uploadmoneyqr_formart_err);
    }
    // data:image/jpeg;base64, 去除开头部分
    moneyqrB64 = moneyqrB64.substring(moneyqrB64.indexOf(",") + 1);
    //Base64解码
    byte[] b64d = Base64.getDecoder().decode(moneyqrB64);
    for (int i = 0; i < b64d.length; ++i) {
      //调整异常数据
      if (b64d[i] < 0) {
        b64d[i] += 256;
      }
    }
    try {
      // 存储上传的moneyqr
      File moneyqrPath = new File(BirdplanetConfig.pathFileUpload);
      if (!moneyqrPath.exists()) {
        moneyqrPath.mkdirs();
      }
      // 3-alipay-190824.jpg
      StringBuilder fileNameSB = new StringBuilder()
          .append(currUserDtlVo.getUid()).append("-")
          .append(currUserDtlVo.getUserType()).append("-")
          .append(DateUtil._sdf(DateTimeFormatterSupport.dfb_YYMMDD, LocalDate.now()))
          .append(".")
          .append(prefix);
      // 保存文件
      OutputStream out =
          new FileOutputStream(BirdplanetConfig.pathFileUpload + fileNameSB);
      out.write(b64d);
      out.flush();
      out.close();
      boolean flag = userService.uploadMoneyQr(currUserDtlVo.getUid(),
          BirdplanetConfig.moneyqrPathPrefix + fileNameSB);
      Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(1);
      dataMap.put("flag", flag);
      return RespDto.succData(dataMap);
    } catch (IOException e) {
      log.error("上传收款码 ERR", e);
      return RespDto.error(ErrorCodes.uploadmoneyqr_err);
    }
  }
}
