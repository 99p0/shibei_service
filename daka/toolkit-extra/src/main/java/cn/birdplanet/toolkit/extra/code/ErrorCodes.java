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
 * @title: ErrorCodes
 * @date 2019-07-18 03:55
 */
@Getter
@ToString
@AllArgsConstructor
public enum ErrorCodes {

  blocklist_ip("-5700", "网络异常，请稍后再试"),
  blocklist_uip("-5701", "无参加权限"),


  rate_limiter("-5001", "访问人数太多,请稍后重试"),
  access_timeout("-5007", "无效的请求"),
  access_replay("-5006", "重复请求"),
  params_err("-5002", "参数有误"),
  token_err("-5003", "无效的请求"),
  missingServletRequestParameter("-5004", "缺失请求参数"),
  RedisConnectionFailure("-5005", "Redis服务异常"),
  user_not_exists("-5007", "此用户不存在"),
  pay_pwd_err("-5008", "支付密码有误"),

  invite_err("-5200", "邀请失败"),
  invite_code_not_exist("-5202", "邀请码不存在"),
  invite_ex_exist("-5203", "存在邀请关系"),
  invite_myself("-5201", "不能邀请自己"),

  recharge_amount_enough("-5302", "钱包金额不足"),
  recharge_amount_true("-5302", "请确保金额范围的正确性"),

  wallet_withdraw_err("-5400", "提现异常,请稍后重试"),
  wallet_not_enough("-5401", "金额不足"),
  wallet_withdraw_minimum_amount("-5402", "最低提现金额10元"),
  wallet_withdraw_max_amount("-5403", "最高限额"),
  wallet_withdraw("-5404", "最低提现金额10元"),
  wallet_withdraw_one_times("-5405", "每天限提现一次"),
  wallet_withdraw_days_times("-5405", "每3天限提现一次"),
  balance_not_enough("-5406", "余额不足"),

  alipay_sdk_err("-5510", "支付宝繁忙，请稍后再试"),
  weixin_sdk_err("-5520", "微信繁忙，请稍后再试"),

  uploadmoneyqr_err("-5600", "上传图片有误"),
  uploadmoneyqr_null("-5601", "不能上传空文件"),
  upload_file_max10mb("-5601", "上传的文件最大支持10MB"),
  uploadmoneyqr_formart_err("-5602", "上传图片格式有误"),

  account_pwd_error("-5901", "用户名或密码有误"),
  account_lock_error("-5101", "账号已锁定"),
  account_not_certified("-5102", "请实名后继续闯关"),

  err("-5000", "ERROR,请稍后重试");

  private final String code;
  private final String desc;


  public static ErrorCodes codeOf(String code) {
    for (ErrorCodes codeEnum : values()) {
      if (codeEnum.code.equalsIgnoreCase(code)) {
        return codeEnum;
      }
    }
    throw new IllegalArgumentException("No matching code for [" + code + "]");
  }


}
