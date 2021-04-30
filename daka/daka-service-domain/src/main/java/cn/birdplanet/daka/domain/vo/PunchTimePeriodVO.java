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
 * @title: PunchTimePeriodVO
 * @date 2019/9/1 16:23
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunchTimePeriodVO {

  private Integer round;

  private Integer times;

  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime startTime;

  @JsonSerialize(using = PunchLDTSerializer.class)
  @JsonDeserialize(using = PunchLDTDeserializer.class)
  private LocalDateTime endTime;

  public PunchTimePeriodVO(Integer round, LocalDateTime startTime, LocalDateTime endTime) {
    this.round = round;
    this.times = 1;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
