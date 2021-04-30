/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;

import java.io.Serializable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class FlagMsgVo implements Serializable {

  private String msg;

  private String flag;

  public FlagMsgVo(String flag, String msg) {
    this.flag = flag;
    this.msg = msg;
  }
}
