/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.commons.util;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.json.JacksonUtil;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: ResponseUtils
 * @description: Response Utils
 * @date 2019-05-28 01:21
 */
@Slf4j
public class ResponseUtils {

  public static boolean output(HttpServletResponse resp, ErrorCodes errorCodes) {
    return ResponseUtils.output(resp, errorCodes.getCode(), errorCodes.getDesc());
  }

  public static boolean output(HttpServletResponse resp, String code, String msg) {
    log.debug("Response output >>> code: {}, msg: {}", code, msg);
    try (OutputStream output = resp.getOutputStream()) {
      resp.setCharacterEncoding("UTF-8");
      resp.setContentType("application/json; charset=UTF-8");
      output.write(JacksonUtil.obj2Json(RespDto.error(code, msg)).getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("Response output >>> {}", e);
    }
    return false;
  }
}
