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
 * @title: AlipayTypeCodes
 * @date 2019-07-18 03:55
 */
@ToString
@Getter
@AllArgsConstructor
public enum AlipayTypeCodes {

  app_pay("app_pay", "App支付"),
  app_oauth("kuaijie", "App登录"),
  def_h5("def_h5", "def_h5"),
  ;

  private final String code;
  private final String desc;

  public static AlipayTypeCodes codeOf(String code) {
    for (AlipayTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }
}
