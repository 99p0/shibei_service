package cn.birdplanet.daka.domain.vo;

import cn.birdplanet.toolkit.extra.support.LocalDateDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class PunchJoinRoundSumByMonthWithDayVO {

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate period;

  private BigDecimal amount;

  private Integer joinedRoundsSum;
  
}
