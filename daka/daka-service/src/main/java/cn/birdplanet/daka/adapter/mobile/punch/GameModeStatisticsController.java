package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthVO;
import cn.birdplanet.daka.domain.vo.PunchJoinRoundSumByMonthWithDayVO;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "打卡*闯关模式 :: 相关操作")
@RestController
@RequestMapping("punch/game-mode/statistics")
public class GameModeStatisticsController extends BaseController {

  @ApiOperation(value = "闯关统计信息", notes = " ")
  @ApiImplicitParams({
  })
  @PostMapping("month")
  public RespDto getData(@RequestAttribute UserDtlVO currUserDtlVo) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    LocalDate currDate = LocalDate.now();
    LocalDate firstDay = currDate.with(TemporalAdjusters.firstDayOfMonth());
    if (currDate.isEqual(firstDay)) {
      // 显示上个月的统计数据
      firstDay = firstDay.minusMonths(1);
    }
    String rkey = RedisConstants.TJ_MONTH_KEY_PREFIX + firstDay.getMonthValue();
    List<PunchJoinRoundSumByMonthVO> monthVOS;
    List<PunchJoinRoundSumByMonthWithDayVO> dayVOS;
    monthVOS =
        (List<PunchJoinRoundSumByMonthVO>) redisUtils.hash_get(rkey,
            "u" + currUserDtlVo.getUid() + "-month");
    dayVOS = (List<PunchJoinRoundSumByMonthWithDayVO>) redisUtils.hash_get(rkey,
        "u" + currUserDtlVo.getUid() + "-daily");

    dataMap.put("month", monthVOS);
    dataMap.put("daily", dayVOS);
    return RespDto.succData(dataMap);
  }
}