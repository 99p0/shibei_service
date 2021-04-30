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
@Table(name = "t_game_mode_template")
public class GameModeTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "seq")
  private Integer seq;

  @Column(name = "title")
  private String title;

  @Column(name = "type")
  private String type;
  @Column(name = "pre_need_joined")
  private String preNeedJoined;

  @Column(name = "cycle_days")
  private Integer cycleDays;

  @Column(name = "min_round")
  private Integer minRound;
  @Column(name = "max_round")
  private Integer maxRound;

  @Column(name = "succ_income_ratio")
  private BigDecimal succIncomeRatio;

  @Column(name = "start_time")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime startTime;

  @Column(name = "end_time")
  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  private LocalTime endTime;

  @Column(name = "deadline_time_join")
  private Integer deadlineTimeJoin;

  @Column(name = "status")
  private String status;

  @Column(name = "is_auto_generate")
  private String isAutoGenerate;

  @Column(name = "is_only_special")
  private String isOnlySpecial;
  @Column(name = "special_uids")
  private String specialUids;
  @Column(name = "barred_uids")
  private String barredUids;

  @Column(name = "is_settle_commission")
  private String isSettleCommission;
  @Column(name = "settle_type")
  private String settleType;
  @Column(name = "bd_bonus_pool")
  private BigDecimal bdBonusPool;

  /** 自动装配 ？？？ */
  @Column(name = "auto_assembly")
  private String autoAssembly;

  /** 9宫格索引 */
  @Column(name = "gird9_idx")
  private Integer gird9Idx;

  /** 支持自动刷新 */
  @Column(name = "is_refresh_auto")
  private String isRefreshAuto;

  @Column(name = "is_high")
  private String isHigh;
  @Column(name = "is_blood")
  private String isBlood;
  @Column(name = "is_one_min")
  private String isOneMin;

  /**
   * 三选一
   */
  @Column(name = "is_3x1")
  private String is3x1;
  @Column(name = "rounds_3x1")
  private String rounds3x1;

  /**
   * 强制打卡
   */
  @Column(name = "is_forced")
  private String isForced;
  @Column(name = "forced_rounds")
  private String forcedRounds;

  /**
   * 延时
   */
  @Column(name = "is_delay")
  private String isDelay;
  @Column(name = "delay_rounds")
  private String delayRounds;
  @Column(name = "delay_time_sec")
  private Integer delayTimeSec;

  @Column(name = "last_generate_period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate lastGeneratePeriod;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;
}
