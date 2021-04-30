package cn.birdplanet.daka.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AlipayContacts
 * @date 2019/8/28 19:14
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlipayContactVO {
  /**
   * 账号的真实姓名
   */
  private String realName;
  /**
   * 账号对应的手机号码
   */
  private String mobile;
  /**
   * 账号的邮箱
   */
  private String email;
  /**
   * 账号的头像链接
   */
  private String avatar;
  /**
   * 支付宝账号 userId
   */
  private String userId;
}
