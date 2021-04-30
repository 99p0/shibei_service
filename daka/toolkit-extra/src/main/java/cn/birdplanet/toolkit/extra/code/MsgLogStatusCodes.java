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
 * @title: AdminTypeCodes
 * @date 2019-07-18 03:55
 */
@ToString
@Getter
@AllArgsConstructor
public enum MsgLogStatusCodes {
  //状态: 0投递中 1投递成功 2投递失败 3已消费
  DELIVER_ING(0, "投递中"),
  DELIVER_SUCCESS(1, "投递成功"),
  DELIVER_FAIL(2, "投递失败"),
  CONSUMED_SUCCESS(3, "已消费"),

  ;

  private final int code;
  private final String desc;

  public static MsgLogStatusCodes codeOf(int code) {
    for (MsgLogStatusCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
