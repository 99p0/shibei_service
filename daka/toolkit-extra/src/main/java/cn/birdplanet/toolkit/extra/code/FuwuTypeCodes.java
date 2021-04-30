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
 * @title: Y or N codes
 * @date 2019-05-09 00:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum FuwuTypeCodes {

  ALIPAY("alipay", "支付宝"),
  WECHAT("wechat", "微信"),
  QQ("qq", "QQ"),
  ;

  private final String code;
  private final String desc;

  public static FuwuTypeCodes codeOf(String code) {
    for (FuwuTypeCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    return null;
  }
}
