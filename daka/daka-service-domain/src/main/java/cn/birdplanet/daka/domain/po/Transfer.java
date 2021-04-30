/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.code.AlipayAccountTypeCodes;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: 提现
 * @date 2019-06-06 16:19
 */
@NoArgsConstructor
@Data
@Slf4j
@Table(name = "t_transfer")
public class Transfer {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "amount")
  private BigDecimal amount;
  @Column(name = "amount_real")
  private BigDecimal amountReal;
  @Column(name = "amount_fee")
  private BigDecimal amountFee;
  @Column(name = "out_biz_no")
  private String outBizNo;

  @Column(name = "payee_account")
  private String payeeAccount;
  @Column(name = "payee_type")
  private String payeeType;

  @Column(name = "order_id")
  private String orderId;

  @Column(name = "status")
  private String status;

  @Column(name = "api_data")
  private String apiData;
  @Column(name = "query_api_data")
  private String queryApiData;
  @Column(name = "remark")
  private String remark;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  public Transfer(long user_id, String outBizNo, BigDecimal amount,
      BigDecimal amountReal, BigDecimal amountFee, String orderId, String apiData,
      AlipayAccountTypeCodes accountTypeCodes, String payeeAccount) {

    this.uid = user_id;
    this.outBizNo = outBizNo;
    this.amount = amount;
    this.amountReal = amountReal;
    this.amountFee = amountFee;
    this.payeeType = accountTypeCodes.getCode();
    this.payeeAccount = payeeAccount;
    this.apiData = apiData;
    this.orderId = orderId;
  }
}
