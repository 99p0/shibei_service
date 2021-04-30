package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
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
@Table(name = "t_normal_mode")
public class NormalMode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "total_people")
  private Integer totalPeople;

  @Column(name = "dummy_total_amount")
  private BigDecimal dummyTotalAmount;

  @Column(name = "dummy_total_people")
  private Integer dummyTotalPeople;

  @Column(name = "fail_total_amount")
  private BigDecimal failTotalAmount;

  @Column(name = "fail_total_people")
  private Integer failTotalPeople;

  @Column(name = "bonus_pool")
  private BigDecimal bonusPool;

  @Column(name = "bonus_method")
  private String bonusMethod;

  @Column(name = "day_method")
  private String dayMethod;

  @Column(name = "daily_fixed_amount")
  private BigDecimal dailyFixedAmount;

  @Column(name = "daily_rate")
  private BigDecimal dailyRate;

  @Column(name = "title")
  private String title;

  @Column(name = "cycle_days")
  private Integer cycleDays;

  @Column(name = "times_oneday")
  private Integer timesOneday;

  @Column(name = "challenge_amount")
  private BigDecimal challengeAmount;

  @Column(name = "multiple")
  private String multiple;

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

  @Column(name = "start_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime startDatetime;

  @Column(name = "end_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endDatetime;

  @Column(name = "is_auto_settle")
  private String isAutoSettle;

  @Column(name = "is_settled")
  private String isSettled;

  @Column(name = "is_settle_bonus_daily")
  private String isSettleBonusDaily;

  @Column(name = "color_hex")
  private String colorHex;

  @Column(name = "status")
  private Integer status;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  public NormalMode(LocalDate period, NormalModeTemplate template) {
    this.period = period;
    //this.totalAmount = totalAmount;
    //this.totalPeople = totalPeople;
    //this.failTotalAmount = failTotalAmount;
    //this.failTotalPeople = failTotalPeople;
    //this.bonusPool = bonusPool;
    this.templateId = template.getId();
    this.bonusMethod = template.getBonusMethod();
    this.title = period.getMonthValue() + "月" + period.getDayOfMonth() + "日 " + template.getTitle();
    this.cycleDays = template.getCycleDays();
    this.timesOneday = template.getTimesOneday();
    this.challengeAmount = template.getChallengeAmount();

    // 奖金的发放形式
    this.dayMethod = template.getDayMethod();
    this.dailyFixedAmount = template.getDailyFixedAmount();
    this.dailyRate = template.getDailyRate();

    this.multiple = template.getMultiple();

    this.punchStartAt1 = template.getPunchStartAt1();
    this.punchEndAt1 = template.getPunchEndAt1();
    this.punchStartAt2 = template.getPunchStartAt2();
    this.punchEndAt2 = template.getPunchEndAt2();
    this.punchStartAt3 = template.getPunchStartAt3();
    this.punchEndAt3 = template.getPunchEndAt3();

    this.startDatetime = LocalDateTime.of(period, BirdplanetConstants.LOCAL_TIME_00_00_00);
    this.endDatetime = LocalDateTime.of(period.plusDays((template.getCycleDays() - 1)),
        BirdplanetConstants.LOCAL_TIME_23_59_59);
    this.colorHex = template.getColorHex();
    this.status = 1;
    this.createdAt = LocalDateTime.now();
    this.isSettleBonusDaily = template.getIsSettleBonusDaily();
    this.isAutoSettle = template.getIsAutoGenerate();
  }
}
