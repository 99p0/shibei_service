package cn.birdplanet.daka.domain.dto;

import cn.birdplanet.toolkit.extra.support.LocalDateDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateSerializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class GameModeDTO {

  private Long id;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  private BigDecimal totalAmount;
  private Integer totalPeople;

  private BigDecimal failTotalAmount;
  private Integer failTotalPeople;

  private Integer minRound;
  private Integer maxRound;

  private BigDecimal succIncomeRatio;

  private Integer gird9Idx;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime startDatetime;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endDatetime;

  private Integer status;

  private String isOnlySpecial;

  private String settleType;
  private BigDecimal bdBonusPool;
  private String isSettleCommission;

  private String title;

  private String type;

  private String preNeedJoined;

  private String isRefreshAuto;
  private String isHigh;
  private String isBlood;
  private String isOneMin;

  private String is3x1;
  private String isDelay;
  private Integer delayTimeSec;
  private Integer delayTime;
}
