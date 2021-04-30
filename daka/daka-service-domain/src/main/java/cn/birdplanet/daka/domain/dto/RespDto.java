/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.dto;

import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class RespDto implements Serializable {

  /**
   * 时间戳
   */
  private String timestamp;

  /**
   * 状态码
   */
  private String code;

  /**
   * 消息
   */
  private String msg;

  /**
   * 数据
   */
  private Object data;

  public RespDto(String code, String msg, Object data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
    this.timestamp = LocalDateTime.now().toString().replace("T", " ");
  }

  public static RespDto error(ErrorCodes errorCodes) {
    return new RespDto(errorCodes.getCode(), errorCodes.getDesc(), "");
  }

  public static RespDto build(ActionVo vo) {
    return vo.isAction() ? succ(vo.getMsg(), vo.getData()) : error(vo.getErrorCodes());
  }

  public static RespDto error(String code, String msg) {
    return new RespDto(code, msg, "");
  }

  public static RespDto error(String code, String msg, Object data) {
    return new RespDto(code, msg, data);
  }

  public static RespDto succ(String code, String msg, Object data) {
    return new RespDto(code, msg, data);
  }

  public static RespDto succ(String msg, Object data) {
    return new RespDto("0", msg, data);
  }

  public static RespDto succMsg(String msg) {
    return new RespDto("0", msg, "");
  }

  public static RespDto succData(Object data) {
    return new RespDto("0", "success", data);
  }
}
