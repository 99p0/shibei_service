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
 * @title: JT:阶梯收益，GD:固定每轮收益，SJ: 随机收益
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum SettleTypeCodes {

  GD("GD", "固定每轮收益"),
  JT("JT", "阶梯收益"),
  BD1("BD1", "1元保底，失败分配"),
  BD0("BD0", "0元保底，失败分配"),
  SJ("SJ", "随机收益"),
  ;

  private final String code;
  private final String desc;

  public static SettleTypeCodes codeOf(String code) {
    for (SettleTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
