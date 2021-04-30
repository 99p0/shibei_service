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
@Table(name = "t_room_mode")
public class RoomMode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "owner_uid")
  private Long ownerUid;

  @Column(name = "period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "total_people")
  private Integer totalPeople;
  @Column(name = "limit_min_people")
  private Integer limitMinPeople;
  @Column(name = "limit_max_people")
  private Integer limitMaxPeople;

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

  @Column(name = "income_ratio")
  private Integer incomeRatio;
  @Column(name = "owner_income_ratio")
  private Integer ownerIncomeRatio;
  @Column(name = "platform_income_ratio")
  private Integer platformIncomeRatio;

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

  @Column(name = "color_hex")
  private String colorHex;

  @Column(name = "is_only_special")
  private String isOnlySpecial;
  @Column(name = "special_uids")
  private String specialUids;

  @Column(name = "blocklist_uids")
  private String blocklistUids;

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

}
