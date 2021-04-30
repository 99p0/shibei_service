/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import java.io.Serializable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ActionVo implements Serializable {

  /**
   * 状态码
   */
  private boolean action;

  /**
   * 消息
   */
  private String msg;

  /**
   * 数据
   */
  private Object data;

  private ErrorCodes errorCodes;

  private ActionVo(ErrorCodes errorCodes) {
    this.action = false;
    this.errorCodes = errorCodes;
  }

  public ActionVo(String msg, Object data) {
    this.action = true;
    this.msg = msg;
    this.data = data;
  }

  public ActionVo(boolean action, String msg) {
    this.action = true;
    this.msg = msg;
  }

  public static ActionVo error(ErrorCodes errorCodes) {
    return new ActionVo(errorCodes);
  }

  public static ActionVo succMsg(String msg) {
    return new ActionVo(msg, "");
  }

  public static ActionVo succData(Object data) {
    return new ActionVo("", data);
  }
}
