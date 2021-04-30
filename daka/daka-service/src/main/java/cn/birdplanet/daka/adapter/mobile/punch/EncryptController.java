/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.UserSecretKey;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IUserSecretKeyService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: UserController
 * @date 2019-08-13 19:23
 */
@Api(tags = "加密信息 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/encrypt")
public class EncryptController extends BaseController {

  @Autowired private IUserSecretKeyService secretKeyService;

  // 您的帐号在另一地点登录，您已被迫下线

  @ApiOperation(value = "获取该用户的公钥", notes = " ")
  @PostMapping(value = "rsa/pk")
  public RespDto getRsaPk(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    UserSecretKey secretKey =
        secretKeyService.getUserSecretKeyIfNullCreateByUid(currUserDtlVo.getUid());
    dataMap.put("pk", secretKey.getRsaPk());
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "上传用户的aes key", notes = " ")
  @PostMapping(value = "aes/upload")
  public RespDto transferAesKey(@RequestAttribute UserDtlVO currUserDtlVo) throws Exception {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
    return RespDto.succData(dataMap);
  }
}
