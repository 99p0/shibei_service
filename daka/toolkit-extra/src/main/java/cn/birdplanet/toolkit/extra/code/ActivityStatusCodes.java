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
public enum ActivityStatusCodes {
  //状态：0 未开放，1报名中，2打卡中，4结束
  _not_open(0, "未开放"),
  PLAZA(1, "活动广场"),
  registered(5, "已报名"),
  _punching(2, "打卡中"),
  _end(4, "结束"),
  ;

  private final int code;
  private final String desc;

  public static ActivityStatusCodes codeOf(int code) {
    for (ActivityStatusCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
