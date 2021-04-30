package cn.birdplanet.daka.domain.dto;

import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class RoomModeDTO {

  private Long id;
  private Long ownerUid;
  private String ownerNickName;
  private String ownerAvatarPath;

  private LocalDate period;

  private BigDecimal totalAmount;

  private Integer limitMinPeople;
  private Integer limitMaxPeople;

  private Integer totalPeople;

  private BigDecimal failTotalAmount;

  private Integer failTotalPeople;

  private BigDecimal bonusPool;

  private String bonusMethod;

  private String dayMethod;

  private BigDecimal dailyFixedAmount;

  private BigDecimal dailyRate;

  private String title;

  private Integer cycleDays;

  private BigDecimal timesOneday;

  private BigDecimal challengeAmount;

  private String multiple;

  private String punchStartAt1;
  private String punchEndAt1;

  private String punchStartAt2;
  private String punchEndAt2;

  private String punchStartAt3;
  private String punchEndAt3;

  @Column(name = "start_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime startDatetime;

  @Column(name = "end_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endDatetime;

  private String colorHex;

  private Integer status;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;
}
