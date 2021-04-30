package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.code.GameModeActivityStatusCodes;
import cn.birdplanet.toolkit.extra.support.LocalDateDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateSerializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "t_game_mode")
public class GameMode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;
  @Column(name = "total_people")
  private Integer totalPeople;

  @Column(name = "fail_total_amount")
  private BigDecimal failTotalAmount;
  @Column(name = "fail_total_people")
  private Integer failTotalPeople;

  @Column(name = "dummy_total_amount")
  private BigDecimal dummyTotalAmount;
  @Column(name = "dummy_total_people")
  private Integer dummyTotalPeople;

  @Column(name = "min_round")
  private Integer minRound;
  @Column(name = "max_round")
  private Integer maxRound;

  @Column(name = "succ_income_ratio")
  private BigDecimal succIncomeRatio;

  @Column(name = "gird9_idx")
  private Integer gird9Idx;

  @Column(name = "bonus_pool")
  private BigDecimal bonusPool;
  @Column(name = "bonus_pool_real")
  private BigDecimal bonusPoolReal;

  @Column(name = "start_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime startDatetime;

  @Column(name = "end_datetime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endDatetime;

  @Column(name = "deadline_time_join")
  private Integer deadlineTimeJoin;

  @Column(name = "status")
  private Integer status;

  @Column(name = "is_only_special")
  private String isOnlySpecial;
  @Column(name = "special_uids")
  private String specialUids;
  @Column(name = "barred_uids")
  private String barredUids;

  @Column(name = "is_auto_settle")
  private String isAutoSettle;
  @Column(name = "is_settled")
  private String isSettled;

  @Column(name = "settle_type")
  private String settleType;
  @Column(name = "bd_bonus_pool")
  private BigDecimal bdBonusPool;
  @Column(name = "is_settle_commission")
  private String isSettleCommission;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "title")
  private String title;

  @Column(name = "type")
  private String type;

  @Column(name = "pre_need_joined")
  private String preNeedJoined;

  @Column(name = "is_refresh_auto")
  private String isRefreshAuto;
  @Column(name = "is_high")
  private String isHigh;
  @Column(name = "is_blood")
  private String isBlood;
  @Column(name = "is_one_min")
  private String isOneMin;

  /**
   * 强制打卡
   */
  @Column(name = "is_forced")
  private String isForced;
  @Column(name = "forced_rounds")
  private String forcedRounds;

  /**
   * 三选一
   */
  @Column(name = "is_3x1")
  private String is3x1;
  @Column(name = "rounds_3x1")
  private String rounds3x1;

  /**
   * 延时
   */
  @Column(name = "is_delay")
  private String isDelay;
  @Column(name = "delay_rounds")
  private String delayRounds;
  @Column(name = "delay_time_sec")
  private Integer delayTimeSec;

  public GameMode(final GameModeTemplate template, final LocalDate period) {
    this.period = period;
    // 今日的早上八点起， 到明天早上七点59结束
    this.startDatetime = LocalDateTime.of(period, template.getStartTime());
    this.endDatetime =
        LocalDateTime.of(period, template.getEndTime()).plusDays(template.getCycleDays());

    this.totalAmount = new BigDecimal("0.000");
    this.totalPeople = 0;
    // 虚拟人
    this.dummyTotalAmount = new BigDecimal("0.000");
    this.dummyTotalPeople = 0;
    //
    this.succIncomeRatio = template.getSuccIncomeRatio();
    // 状态更改
    this.status = GameModeActivityStatusCodes.normal.getCode();
    this.createdAt = LocalDateTime.now();
    //
    this.title = template.getTitle();
    this.type = template.getType();
    this.preNeedJoined = template.getPreNeedJoined();
    this.minRound = template.getMinRound();
    this.maxRound = template.getMaxRound();
    // 结算佣金关系
    this.isSettleCommission = template.getIsSettleCommission();
    this.settleType = template.getSettleType();
    this.setBdBonusPool(template.getBdBonusPool());
    // 活动截止前的时间
    this.deadlineTimeJoin = template.getDeadlineTimeJoin();
    // 仅支持特殊人群参加
    this.isOnlySpecial = template.getIsOnlySpecial();
    this.specialUids = template.getSpecialUids();
    // 禁止参加的人群
    this.barredUids = template.getBarredUids();
    // 是否高倍场
    this.isHigh = template.getIsHigh();
    // 是否属于回血房
    this.isBlood = template.getIsBlood();
    // 一分钟场
    this.isOneMin = template.getIsOneMin();
    // 是否支持三选一
    this.is3x1 = template.getIs3x1();
    this.rounds3x1 = template.getRounds3x1();
    // 是否支持延迟打卡
    this.isDelay = template.getIsDelay();
    this.delayRounds = template.getDelayRounds();
    this.delayTimeSec = template.getDelayTimeSec();
    // 指定强制签到的轮数
    this.isForced = template.getIsForced();
    this.forcedRounds = template.getForcedRounds();
    // 9宫格 归属标示
    this.gird9Idx = template.getGird9Idx();
    // 自动刷新
    this.isRefreshAuto = template.getIsRefreshAuto();
  }
}
