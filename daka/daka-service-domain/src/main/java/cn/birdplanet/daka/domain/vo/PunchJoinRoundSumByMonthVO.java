package cn.birdplanet.daka.domain.vo;

import java.math.BigDecimal;
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
public class PunchJoinRoundSumByMonthVO {

  private BigDecimal amount;

  private Integer joinedRoundsSum;
}
