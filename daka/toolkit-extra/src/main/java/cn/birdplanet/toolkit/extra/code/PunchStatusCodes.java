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
 * @title: PunchStatusCodes
 * @description: 参加打卡的状态
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum PunchStatusCodes {

  registered(5, "已报名"),
  not_join(0, "未参加"),
  joining(1, "打卡中"),
  success(2, "打卡成功，继续下一轮"),
  fail(3, "打卡失败"),
  checkin(4, "立即签到"),
  ;

  private final int code;
  private final String desc;

  public static PunchStatusCodes codeOf(int code) {
    for (PunchStatusCodes codeEnum : values()) {
      if (codeEnum.code == (code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
