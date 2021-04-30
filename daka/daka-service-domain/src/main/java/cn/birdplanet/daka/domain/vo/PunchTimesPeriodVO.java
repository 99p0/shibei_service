package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.toolkit.extra.support.PunchLDTDeserializer;
import cn.birdplanet.toolkit.extra.support.PunchLDTSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchTimesPeriodVO
 * @date 2019/9/1 16:23
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunchTimesPeriodVO {

  private Integer round;

  /**
   * 是否强制
   */
  private String isForced;
  /**
   * 是否延迟
   */
  private String isDelay;
  /**
   * 是否三选一
   */
  private String is3x1;
  /**
   *
   */
  private Integer times;

  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime startTime;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime endTime;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime startTime2;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime endTime2;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime startTime3;
  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime endTime3;
}
