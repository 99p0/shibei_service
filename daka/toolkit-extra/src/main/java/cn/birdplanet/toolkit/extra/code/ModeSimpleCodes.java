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
 * @title: ModeSimpleCodes
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum ModeSimpleCodes {

  GameMode("G", "闯关模式"),
  NormalMode("N", "常规模式"),
  RoomMode("R", "房间模式"),
  ;

  private final String code;
  private final String desc;

  public static ModeSimpleCodes codeOf(String code) {
    for (ModeSimpleCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
