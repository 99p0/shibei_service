/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.code.BrokerageDtlTypeCodes;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
 * @title: 佣金明细
 * @date 2019-06-06 16:19
 */
@Data
@Slf4j
@NoArgsConstructor
@Table(name = "t_brokerage_dtl")
public class BrokerageDtl {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "type")
  private Integer type;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "invitees_uid")
  private Long inviteesUid;

  @Column(name = "content")
  private String content;

  @Column(name = "amount")
  private String amount;

  @Column(name = "is_read")
  private Integer isRead;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  public BrokerageDtl(BrokerageDtlTypeCodes typeCode, long uid, long inviteesUid, String content,
      String amount) {
    this.type = typeCode.getCode();
    this.uid = uid;
    this.inviteesUid = inviteesUid;
    this.content = content;
    this.amount = typeCode.getCode() == 2 ? "-" + amount : "+" + amount;
  }

  public BrokerageDtl(BrokerageDtlTypeCodes typeCode, long uid, String content, String amount) {
    this.type = typeCode.getCode();
    this.uid = uid;
    this.content = content;
    this.amount = typeCode.getCode() == 2 ? "-" + amount : "+" + amount;
  }
}
