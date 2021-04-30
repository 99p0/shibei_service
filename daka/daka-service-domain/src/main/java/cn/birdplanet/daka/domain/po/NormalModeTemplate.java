package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.support.LocalDateDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateSerializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import cn.birdplanet.toolkit.extra.support.LocalTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@Table(name = "t_normal_mode_template")
public class NormalModeTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "cycle_days")
  private Integer cycleDays;

  @Column(name = "times_oneday")
  private Integer timesOneday;

  @Column(name = "challenge_amount")
  private BigDecimal challengeAmount;

  @Column(name = "day_method")
  private String dayMethod;

  @Column(name = "daily_fixed_amount")
  private BigDecimal dailyFixedAmount;

  @Column(name = "daily_rate")
  private BigDecimal dailyRate;

  @Column(name = "punch_start_at_1")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchStartAt1;

  @Column(name = "punch_end_at_1")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchEndAt1;

  @Column(name = "punch_start_at_2")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchStartAt2;

  @Column(name = "punch_end_at_2")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchEndAt2;

  @Column(name = "punch_start_at_3")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchStartAt3;

  @Column(name = "punch_end_at_3")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime punchEndAt3;

  @Column(name = "multiple")
  private String multiple;

  @Column(name = "bonus_method")
  private String bonusMethod;

  @Column(name = "is_auto_generate")
  private String isAutoGenerate;

  @Column(name = "is_settle_bonus_daily")
  private String isSettleBonusDaily;

  @Column(name = "interval_days")
  private Integer intervalDays;

  @Column(name = "last_generate_period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate lastGeneratePeriod;

  @Column(name = "color_hex")
  private String colorHex;
  @Column(name = "status")
  private String status;

  @Column(name = "is_time_change")
  private String isTimeChange;
  @Column(name = "time_change_last")
  private String timeChangeLast;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;
}
