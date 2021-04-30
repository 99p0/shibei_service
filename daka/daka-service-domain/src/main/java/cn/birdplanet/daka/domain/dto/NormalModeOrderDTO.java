package cn.birdplanet.daka.domain.dto;


import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.NormalModeRound;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class NormalModeOrderDTO {

  private Long id;
  private Long uid;
  private Long activityId;
  private LocalDate period;
  private BigDecimal amount;
  private Integer multiple;
  private Integer status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * 活动信息
   */
  private NormalModeDTO normalModeDTO;
  /**
   * 打卡的详情
   */
  private List<NormalModeRound> rounds;

  public NormalModeOrderDTO(NormalModeOrder order) {
    this.id = order.getId();
    this.uid = order.getUid();
    this.activityId = order.getActivityId();
    this.period = order.getPeriod();
    this.amount = order.getAmount();
    this.multiple = order.getMultiple();
    this.status = order.getStatus();
    this.createdAt = order.getCreatedAt();
    this.updatedAt = order.getUpdatedAt();
  }
}
