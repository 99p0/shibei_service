/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
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
public class UserDtlVO {

  private Long id;

  private Long uid;

  private String userType;

  private Integer status;

  private String invitationCode;

  private String avatarPath;

  private String moneyqr;

  private String nickName;
  private String gender;

  private String isCertified;
  private String isFirstWithdraw;

  private String inviterCode;

  private String brokerageWithdrawalSwitch;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  public String getAvatarPath() {
    return StringUtils.isBlank(avatarPath) ? BirdplanetConstants.AVATAR_DEF_URL : avatarPath;
  }

  public String getNickName() {
    return StringUtils.isBlank(nickName) ? "未设置昵称" : nickName;
  }
}
