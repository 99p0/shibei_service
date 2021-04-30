package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
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
@Table(name = "t_game_mode_settle_record")
public class GameModeSettleRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "activity_id")
  private Long activityId;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "period")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  @Column(name = "joined_round")
  private Integer joinedRound;

  @Column(name = "pay_amount")
  private BigDecimal payAmount;

  @Column(name = "bonus")
  private BigDecimal bonus;
  @Column(name = "benefits")
  private BigDecimal benefits;

  @Column(name = "inviter_brokerage")
  private BigDecimal inviterBrokerage;

  @Column(name = "current_round")
  private Integer currentRound;

  @Column(name = "punch_successful")
  private String punchSuccessful;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  public GameModeSettleRecord(GameModeOrder order) {
    this.activityId = order.getActivityId();
    this.uid = order.getUid();
    this.period = order.getPeriod();
    this.joinedRound = order.getJoinedRounds();
    this.payAmount = order.getAmount();
    this.currentRound = order.getCurrentRound();
    this.punchSuccessful = order.getStatus() == PunchStatusCodes.success.getCode() ? "Y" : "N";
  }
}
