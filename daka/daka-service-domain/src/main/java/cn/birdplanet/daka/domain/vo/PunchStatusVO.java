package cn.birdplanet.daka.domain.vo;


import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import cn.birdplanet.toolkit.extra.support.PunchLDTDeserializer;
import cn.birdplanet.toolkit.extra.support.PunchLDTSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchStatusVO
 * @date 2019/8/29 17:54
 */
@Slf4j
@Data
@NoArgsConstructor
public class PunchStatusVO {

  /** 是否需要输入邀请码的输入框 */
  private Boolean needInpInvitedCode;
  /** 我的余额 */
  private String balance;

  /** 活动ID */
  private Long aid;

  /** 距离活动的结束的秒数 */
  private Long activityEndSeconds;
  /** 距离活动的结束时间 */
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime activityEndTime;
  /** 标题 */
  private String title;
  /** 期数 */
  private String period;
  /** 类型 */
  private String type;
  /** 最小/最大打卡轮数 */
  private Integer minRound;
  private Integer maxRound;
  /** 总金额 */
  private Integer totalAmount;
  /** 总人数 */
  private Integer totalPeople;

  /** 参加状态 */
  private Integer punchStatus;

  /** 已参加？轮数 */
  private Integer joinedRounds;
  /** 当前打卡的轮次 */
  private Integer currRound;
  /** 当前轮参加的时间 */
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime currRoundJoinedTime;
  /** 当前轮参加的时间 */
  private Long currRoundJoinTimeSeconds;
  /** 距离打卡开始的秒数 */
  private Long punchingSeconds;
  private Long punchingEndSeconds;

  private String isSettleCommission;
  private Integer deadlineTimeJoin;

  private String isRefreshAuto;
  private String isForced;

  private String isHigh;
  private String isBlood;

  private String is3x1;
  private Integer is3x1True;

  private String isDelay;
  private Integer delayTimeSec;

  /** 打卡的开始 And 结束时间 */
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchStartTime;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchEndTime;

  @Column(name = "starttime_2")
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchStartTime2;
  @Column(name = "endtime_2")
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchEndTime2;

  @Column(name = "starttime_3")
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchStartTime3;
  @Column(name = "endtime_3")
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime punchEndTime3;

  /** 系统消息 */
  private Boolean hasNotice;
  private String noticeContent;

  /**9宫格*/
  private List<GameModeGrid9> grid9;
}
