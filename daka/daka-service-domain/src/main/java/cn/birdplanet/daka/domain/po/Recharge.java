/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.TradeStatusCodes;
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
 * @title: 充值表
 * @date 2019-06-06 16:19
 */
@NoArgsConstructor
@Data
@Slf4j
@Table(name = "t_recharge")
public class Recharge {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "channel")
  private String channel;
  @Column(name = "ordersn")
  private String ordersn;

  @Column(name = "trade_no")
  private String tradeNo;

  @Column(name = "trade_status")
  private String tradeStatus;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "body_callback")
  private String bodyCallback;
  @Column(name = "body_req")
  private String bodyReq;

  @Column(name = "return_url")
  private String returnUrl;
  @Column(name = "notify_url")
  private String notifyUrl;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  public Recharge(long uid, TradeChannelCodes tradeChannelCode, String ordersn,
      String bodyReq, BigDecimal amount, TradeStatusCodes tradeStatusCode) {
    this.uid = uid;
    this.channel = tradeChannelCode.getCode();
    this.ordersn = ordersn;
    this.tradeStatus = tradeStatusCode.getCode();
    this.bodyReq = bodyReq;
    this.amount = amount;
    this.createdAt = LocalDateTime.now();
  }
}
