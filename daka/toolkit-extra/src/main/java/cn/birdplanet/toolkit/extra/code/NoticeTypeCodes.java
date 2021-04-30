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
 * @title: NoticeTypeCodes
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum NoticeTypeCodes {

  invited_user_join(1, "受邀用户加入"),
  punch_succ(2, "闯关成功"),
  punch_err(3, "闯关失败"),
  ;

  private final int code;
  private final String desc;

  public static NoticeTypeCodes codeOf(int code) {
    for (NoticeTypeCodes codeEnum : values()) {
      if (codeEnum.code == code) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
