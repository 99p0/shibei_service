/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.support.LocalDateDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateSerializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: User
 * @date 2019-06-06 16:19
 */
@Data
@Slf4j
@NoArgsConstructor
@Table(name = "t_user")
public class User {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "user_type")
  private String userType;
  @Column(name = "open_uid")
  private String openUid;
  @Column(name = "openid")
  private String openid;
  @Column(name = "ali_user_account")
  private String aliUserAccount;

  @Column(name = "status")
  private Integer status;

  @Column(name = "balance")
  private BigDecimal balance;
  @Column(name = "wallet")
  private BigDecimal wallet;

  @Column(name = "income_sum")
  private BigDecimal incomeSum;

  @Column(name = "brokerage")
  private BigDecimal brokerage;
  @Column(name = "brokerage_sum")
  private BigDecimal brokerageSum;
  @Column(name = "brokerage_freeze")
  private BigDecimal brokerageFreeze;
  @Column(name = "brokerage_withdrawal_switch")
  private String brokerageWithdrawalSwitch;

  @Column(name = "invitation_code")
  private String invitationCode;

  @Column(name = "nick_name")
  private String nickName;

  @Column(name = "name")
  private String name;

  @Column(name = "avatar_path")
  private String avatarPath;

  @Column(name = "moneyqr")
  private String moneyqr;

  @Column(name = "mobile")
  private String mobile;

  @Column(name = "cert_type")
  private Integer certType;

  @Column(name = "cert_no")
  private String certNo;

  @Column(name = "gender")
  private String gender;

  @Column(name = "is_certified")
  private String isCertified;

  @Column(name = "is_first_withdraw")
  private String isFirstWithdraw;

  @Column(name = "need_inp_invited_code")
  private String needInpInvitedCode;

  @Column(name = "inviter_code")
  private String inviterCode;
  @Column(name = "inviter_uid")
  private Long inviterUid;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "created_date")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate createdDate;

  public static String convertGenderByAlipay(String gender) {
    return StringUtils.isNotBlank(gender) ? gender.toUpperCase() : "";
  }

  public static String convertIsCertifiedByAlipay(String isCertified) {
    return (StringUtils.isNotBlank(isCertified) && "T".equalsIgnoreCase(isCertified)) ? "Y" : "N";
  }

  public String getNickName() {
    return StringUtils.isBlank(nickName) ? "未设置昵称" : nickName;
  }
}
