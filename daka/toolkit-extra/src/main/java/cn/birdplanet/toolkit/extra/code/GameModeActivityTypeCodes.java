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
 * @title: GameModeActivityTypeCodes
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum GameModeActivityTypeCodes {

  A("A", "小鸟闯关1K"),
  B("B", "小鸟闯关2k"),
  C("C", "小鸟闯关4k"),
  D("D", "小鸟闯关3k"),
  E("E", "小鸟闯关500"),
  F("F", "小鸟闯关5k"),
  W("W", "10000回血房间"),
  G("G", "延时闯关1k"),
  H("H", "延时闯关2k"),
  I("I", "延时闯关500A"),
  J("J", "延时闯关500B"),
  K("K", ""),
  L("L", ""),
  M("M", ""),
  N("N", ""),
  O("O", ""),
  P("P", ""),
  Q("Q", ""),
  R("R", ""),
  S("S", ""),
  T("T", ""),
  U("U", ""),
  V("V", ""),
  X("X", ""),
  Y("Y", ""),
  Z("Z", ""),
  ;

  private final String code;
  private final String desc;

  public static GameModeActivityTypeCodes codeOf(String code) {
    for (GameModeActivityTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
