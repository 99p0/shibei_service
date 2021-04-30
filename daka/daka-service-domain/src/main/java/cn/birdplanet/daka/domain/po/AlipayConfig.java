/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.po;

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
 * @title: t_alipay_config
 * @date 2019-07-19 15:04
 */
@NoArgsConstructor
@Slf4j
@Data
@Table(name = "t_alipay_config")
public class AlipayConfig {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "pid")
  private String pid;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "server_url")
  private String serverUrl;

  @Column(name = "private_key")
  private String privateKey;

  @Column(name = "public_key")
  private String publicKey;

  @Column(name = "public_key_ali")
  private String publicKeyAli;

  @Column(name = "sign_type")
  private String signType;

  @Column(name = "charset")
  private String charset;

  @Column(name = "format")
  private String format;

  @Column(name = "channels")
  private String channels;

  @Column(name = "status")
  private String status;

  @Column(name = "is_def")
  private String isDef;

  @Column(name = "app_oauth_def")
  private String appOauthDef;

  @Column(name = "pay_app_def")
  private String payAppDef;

  @Column(name = "notify_url")
  private String notifyUrl;

  @Column(name = "return_url")
  private String returnUrl;

  @Column(name = "redirect_url")
  private String redirectUrl;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;
}
