/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

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
 * @title: 钱包
 * @date 2019-06-06 16:19
 */
@Data
@Slf4j
@NoArgsConstructor
@Table(name = "t_gm_statistics_month")
public class GmStatisticsByMonth {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "per_month")
  private String perMonth;

  @Column(name = "type")
  private String type;

  @Column(name = "joined_days")
  private Integer joined_days;

  @Column(name = "joined_info")
  private String joinedInfo;

  @Column(name = "joined_fail_times")
  private Integer joinedFailTimes;

  @Column(name = "joined_fail_info")
  private String joinedFailInfo;

  @Column(name = "joined_succ_times")
  private Integer joinedSuccTimes;

  @Column(name = "checkin_times")
  private Integer checkinTimes;

  @Column(name = "fail_amount")
  private BigDecimal failAmount;

  @Column(name = "benefit_amount")
  private BigDecimal benefitAmount;

  @Column(name = "bonus_amount")
  private BigDecimal bonusAmount;

  @Column(name = "brokerage_amount")
  private BigDecimal brokerageAmount;

  @Column(name = "inviter_brokerage_amount")
  private BigDecimal inviterBrokerageAmount;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

}
