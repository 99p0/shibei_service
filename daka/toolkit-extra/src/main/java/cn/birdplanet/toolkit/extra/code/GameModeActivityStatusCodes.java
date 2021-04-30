/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.extra.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: ActivityStatusCodes
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum GameModeActivityStatusCodes {

  normal(1, "正常"),
  finish(2, "结束"),
  settle(4, "已结算"),
  ;

  private final int code;
  private final String desc;

  public static GameModeActivityStatusCodes codeOf(int code) {
    for (GameModeActivityStatusCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
