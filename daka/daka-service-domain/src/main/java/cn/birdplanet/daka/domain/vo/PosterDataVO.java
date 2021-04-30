package cn.birdplanet.daka.domain.vo;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class PosterDataVO {

  private String avatarPath;
  private String invitationCode;
  private String nickName;

  private Long joined_days;
  private Integer checkin_times;
  private BigDecimal income_sum;
}
