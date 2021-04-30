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
 * @title: PayeeAccountTypeCodes
 * @date 2019-07-18 03:04
 */
@Getter
@ToString
@AllArgsConstructor
public enum TransferStatusCodes {

  SUCCESS("SUCCESS", "成功（配合\"单笔转账到银行账户接口\"产品使用时, 同一笔单据多次查询有可能从成功变成退票状态）"),
  FAIL("FAIL", "失败（具体失败原因请参见error_code以及fail_reason返回值"),
  INIT("INIT", "等待处理"),
  DEALING("DEALING", "处理中；"),
  REFUND("SUCCESS", "退票（仅配合\"单笔转账到银行账户接口\"产品使用时会涉及, 具体退票原因请参见fail_reason返回值）"),
  UNKNOWN("UNKNOWN", "状态未知");

  private final String code;
  private final String desc;

  public static TransferStatusCodes codeOf(String code) {
    for (TransferStatusCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }

}
