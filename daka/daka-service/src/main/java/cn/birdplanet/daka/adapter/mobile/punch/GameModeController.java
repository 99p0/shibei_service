/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.GameModeDTO;
import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.GameModeGear;
import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchStatusVO;
import cn.birdplanet.daka.domain.vo.PunchTimePeriodVO;
import cn.birdplanet.daka.domain.vo.PunchTimesPeriodVO;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.commons.util.ActivityUtils;
import cn.birdplanet.daka.infrastructure.commons.util.IpUtil;
import cn.birdplanet.daka.infrastructure.service.IGameModeService;
import cn.birdplanet.daka.infrastructure.service.ISysNoticeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import cn.birdplanet.toolkit.extra.code.ModeSimpleCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import cn.birdplanet.toolkit.ratelimit.redis.RedisRateLimit;
import cn.birdplanet.toolkit.ratelimit.redis.RedisRateLimitType;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ??????[uncle.yang@outlook.com]
 */
@Slf4j
@Api(tags = "??????*???????????? :: ????????????")
@RestController
@RequestMapping("punch/game-mode")
public class GameModeController extends BaseController {

  @Autowired private IGameModeService gameModeService;
  @Autowired private ISysNoticeService sysNoticeService;
  @Autowired private IUserService userService;

  @ApiOperation(value = "??????????????????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "pageNum", dataType = "int", value = "??????"),
      @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", value = "????????????"),
  })
  @RedisRateLimit(limitType = RedisRateLimitType.UID_IP_URI, period = 60, count = 40)
  @PostMapping("activities/available")
  public RespDto getAvailableActivities(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "") String type,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "20") int pageSize) {
    LocalDate currPeriod = gameModeService.getCurrPeriod();
    // ?????????????????????
    String key = RedisConstants.GAME_MODE_LIST_KEY_PREFIX + currPeriod;
    // ????????????
    List<GameModeDTO> activities = null;
    try {
      activities = (List<GameModeDTO>) redisUtils.get(key);
    } catch (Exception e) {
      log.error("???????????????????????????{}", key);
      redisUtils.del(key);
    }
    if (null == activities || activities.isEmpty()) {
      activities = gameModeService.getAvailableActivities(currPeriod, true);
    }
    // ?????? ??????????????? ????????????
    if (StringUtils.isNotBlank(type)) {
      activities = (List<GameModeDTO>) redisUtils.get(key + type.toLowerCase());
    }
    //
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("list", activities);
    return RespDto.succData(dataMap);
  }

  @RedisRateLimit(limitType = RedisRateLimitType.UID_IP_URI, period = 60, count = 40)
  @ApiOperation(value = "???????????????????????????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "int", value = "??????ID"),
  })
  @PostMapping("activity/status")
  public RespDto activityStatus(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam long aid,
      @RequestParam(required = false, defaultValue = "") String deviceInfo,
      @RequestParam(required = false, defaultValue = "") String locationAlipay,
      Device device, HttpServletRequest request) {
    GameMode activity = gameModeService.getActivityById(aid);
    if (null == activity) {
      return RespDto.error(ErrorCodes.err);
    }
    // ????????????
    // ????????????????????????
    GameModeOrder order =
        gameModeService.getOrderByActivityId(currUserDtlVo.getUid(), activity.getId(), false);
    //
    PunchStatusVO vo = new PunchStatusVO();

    // ????????????
    vo.setIsRefreshAuto(activity.getIsRefreshAuto());

    vo.setIs3x1(activity.getIs3x1());
    vo.setIsForced(activity.getIsForced());
    vo.setIsDelay(activity.getIsDelay());
    vo.setDelayTimeSec(activity.getDelayTimeSec());

    vo.setIsHigh(activity.getIsHigh());
    vo.setIsBlood(activity.getIsBlood());

    vo.setAid(activity.getId());
    vo.setTitle(activity.getTitle());
    vo.setType(activity.getType());
    vo.setMinRound(activity.getMinRound());
    vo.setMaxRound(activity.getMaxRound());
    vo.setActivityEndTime(activity.getEndDatetime());
    vo.setActivityEndSeconds(PunchUtils.getPunchingSeconds(activity.getEndDatetime()));
    vo.setPeriod(PunchUtils.punchPeriod(activity.getPeriod()));
    // ????????????+ ????????????
    vo.setTotalPeople(activity.getTotalPeople() + activity.getDummyTotalPeople());
    vo.setTotalAmount(activity.getTotalAmount().add(activity.getDummyTotalAmount()).intValue());
    //
    vo.setIsSettleCommission(activity.getIsSettleCommission());
    vo.setDeadlineTimeJoin(activity.getDeadlineTimeJoin());

    int currentRound = 0;

    // ????????????
    if (null == order) {
      vo.setPunchStatus(PunchStatusCodes.not_join.getCode());
    } else {
      // ?????????
      if (PunchStatusCodes.joining.getCode() == order.getStatus()) {

        GameModeRound punchRound =
            gameModeService.getCurrPunchRoundsForRedis(currUserDtlVo.getUid(), order.getId(),
                order.getCurrentRound());

        currentRound = punchRound.getRound();
        vo.setIsForced(activity.getForcedRounds().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
            && ActivityUtils.hasRound(activity.getForcedRounds(), currentRound) ? YesOrNoCodes.YES
            .getCode() : YesOrNoCodes.NO.getCode());

        vo.setIs3x1(activity.getIs3x1().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
            && ActivityUtils.hasRound(activity.getRounds3x1(), currentRound) ? YesOrNoCodes.YES
            .getCode() : YesOrNoCodes.NO.getCode());

        vo.setIsDelay(activity.getIsDelay().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
            && ActivityUtils.hasRound(activity.getDelayRounds(), currentRound) ? YesOrNoCodes.YES
            .getCode() : YesOrNoCodes.NO.getCode());

        vo.setCurrRound(punchRound.getRound());
        // ????????????????????????????????????????????????
        vo.setCurrRoundJoinTimeSeconds(
            PunchUtils.getPunchingSeconds(punchRound.getCreatedAt(), LocalDateTime.now()));
        vo.setCurrRoundJoinedTime(punchRound.getCreatedAt());

        vo.setPunchStartTime(punchRound.getStartTime());
        vo.setPunchEndTime(punchRound.getEndTime());
        //vo.setIs3x1True(punchRound.getIsTrue3x1());
        vo.setPunchStartTime2(punchRound.getStartTime2());
        vo.setPunchEndTime2(punchRound.getEndTime2());
        vo.setPunchStartTime3(punchRound.getStartTime3());
        vo.setPunchEndTime3(punchRound.getEndTime3());

        // ??????????????????????????????
        LocalDateTime startTimeTrue = punchRound.getStartTime();
        LocalDateTime endTimeTrue = punchRound.getEndTime();

        if (StringUtils.isNotBlank(activity.getIs3x1())
            && YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIs3x1())) {
          // ???????????????
          if (punchRound.getIsTrue3x1() == 2) {
            startTimeTrue = punchRound.getStartTime2();
            endTimeTrue = punchRound.getEndTime2();
          } else if (punchRound.getIsTrue3x1() == 3) {
            // ???????????????
            startTimeTrue = punchRound.getStartTime3();
            endTimeTrue = punchRound.getEndTime3();
          }
        }
        // ???????????????????????????
        vo.setPunchingSeconds(PunchUtils.getPunchingSeconds(startTimeTrue));
        vo.setPunchingEndSeconds(PunchUtils.getPunchingSeconds(endTimeTrue));
        //
        LocalDateTime now = LocalDateTime.now();
        // ??????????????????
        if (now.isAfter(startTimeTrue) && now.isBefore(endTimeTrue)) {
          vo.setPunchStatus(PunchStatusCodes.checkin.getCode());
        } else if (now.isAfter(endTimeTrue)) {
          // ??????????????????????????? ??????????????? & ????????????????????????
          gameModeService.checkin(currUserDtlVo.getUid(), activity, order, now,
              IpUtil.getIpAddr(request), device, deviceInfo, locationAlipay);
          vo.setPunchStatus(PunchStatusCodes.fail.getCode());
        } else {
          vo.setPunchStatus(PunchStatusCodes.joining.getCode());
        }
      } else { //
        vo.setPunchStatus(PunchStatusCodes.codeOf(order.getStatus()).getCode());
      }
    }

    vo.setJoinedRounds(null != order ? order.getJoinedRounds() : 0);
    // ????????????
    User userInfo = userService.getByUidFromRedis(currUserDtlVo.getUid());
    vo.setBalance(String.valueOf(userInfo.getBalance().intValue()));
    // ???????????????????????????????????? ??????????????????????????????
    vo.setNeedInpInvitedCode(StringUtils.isBlank(userInfo.getInviterCode())
        && YesOrNoCodes.YES.getCode().equalsIgnoreCase(userInfo.getNeedInpInvitedCode()));
    // ????????????????????????
    String notice =
        sysNoticeService.getSysNoticeStrWithType(ModeSimpleCodes.GameMode, activity.getType());
    vo.setHasNotice(StringUtils.isNotBlank(notice));
    vo.setNoticeContent(notice);
    // 9??????
    List<GameModeGrid9> grid9List =
        gameModeService.getGrid9Data(GameModeActivityTypeCodes.codeOf(activity.getType()));
    vo.setGrid9(grid9List);
    return RespDto.succData(vo);
  }

  @RedisRateLimit(limitType = RedisRateLimitType.UID_IP_URI, period = 60, count = 15)
  @ApiOperation(value = "????????????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "amount", dataType = "int", value = "??????"),
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "long", value = "??????ID"),
  })
  @PostMapping("activity/join")
  public RespDto activityJoin(@RequestAttribute UserDtlVO currUserDtlVo, @RequestParam int amount,
      @RequestParam(required = false, defaultValue = "") String deviceInfo,
      @RequestParam(required = false, defaultValue = "") String locationAlipay,
      @RequestParam long aid, Device device, HttpServletRequest request) {

    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    final GameMode activity = gameModeService.getActivityById(aid);

    // ???????????????????????? ??????????????????
    LocalDateTime nowLDT = LocalDateTime.now();
    // ??????????????? ??????????????????
    LocalDateTime stopJoinLdt = activity.getEndDatetime()
        .minusMinutes(null == activity.getDeadlineTimeJoin() ? 150 : activity
            .getDeadlineTimeJoin());

    if (nowLDT.isAfter(stopJoinLdt)) {
      dataMap.put("flag", "stop");
      dataMap.put("msg", "????????????????????????");
      return RespDto.succData(dataMap);
    }

    // ??????????????????
    if (StringUtils.isNotBlank(activity.getBarredUids())
        && activity.getBarredUids().contains("," + currUserDtlVo.getUid().toString() + ",")) {
      dataMap.put("flag", "false");
      dataMap.put("msg", "???????????????");
      return RespDto.succData(dataMap);
    }

    // ?????????????????????????????????
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsOnlySpecial())) {
      // ??????????????????????????????
      if (StringUtils.isBlank(activity.getSpecialUids())
          || !activity.getSpecialUids().contains("," + currUserDtlVo.getUid().toString() + ",")) {
        dataMap.put("flag", "false");
        dataMap.put("msg", "??????????????????");
        return RespDto.succData(dataMap);
      }
    }
    // ????????????????????? ???????????????????????????+??????????????????????????????
    if (activity.getMinRound() > 1) {
      // ????????????????????? ????????????
      List<GameModeGear> gearList =
          gameModeService.getAllGearDataByType(
              GameModeActivityTypeCodes.codeOf(activity.getType()));
      // ?????????????????????
      long needTimeMin = gearList.stream()
          .filter(gameModeGear -> gameModeGear.getRound() <= activity.getMinRound())
          .mapToLong(GameModeGear::getRangeMax)
          .sum() / 60;
      // ????????????
      stopJoinLdt =
          activity.getEndDatetime().minusMinutes(activity.getDeadlineTimeJoin() + needTimeMin);
      if (nowLDT.isAfter(stopJoinLdt)) {
        dataMap.put("flag", "stop");
        dataMap.put("msg", "?????????????????????????????????");
        return RespDto.succData(dataMap);
      }
    }
    // ???????????????????????????????????????
    List<GameModeGrid9> grid9List =
        gameModeService.getGrid9Data(GameModeActivityTypeCodes.codeOf(activity.getType()));
    // ???????????????????????? ??????????????????????????????
    if (!grid9List.stream()
        .filter(gird9 -> gird9.getAmount().intValue() == amount)
        .findAny()
        .isPresent()) {
      dataMap.put("flag", "false");
      dataMap.put("msg", "???????????????");
      return RespDto.succData(dataMap);
    }
    //
    BigDecimal amountDecimal = NumberUtil.format_s3(new BigDecimal(amount));
    User user = userService.getByUid(currUserDtlVo.getUid());
    log.debug("join >>> user.balance={}, amount={}", user.getBalance(), amountDecimal);
    if (user.getBalance().compareTo(amountDecimal) == -1) {
      dataMap.put("flag", "not_enough");
      dataMap.put("msg", ErrorCodes.balance_not_enough.getDesc());
      return RespDto.succData(dataMap);
    }
    // ???????????????????????????????????? ????????????????????? ????????????
    GameModeOrder punchOrder =
        gameModeService.getOrderByActivityId(currUserDtlVo.getUid(), activity.getId(), false);
    if (null != punchOrder) {
      //  ??????????????????????????????????????? ???????????????
      if (punchOrder.getStatus() != PunchStatusCodes.fail.getCode()) {
        dataMap.put("flag", "false");
        dataMap.put("msg", "??????????????????");
        return RespDto.succData(dataMap);
      }
    }
    // ????????????????????????
    if (StringUtils.isNotBlank(activity.getPreNeedJoined())) {
      String preNeedJoinedTypeCodes = Arrays.asList(activity.getPreNeedJoined().split(","))
          .stream()
          .distinct()
          .filter(str -> StringUtils.isNotBlank(str))
          .map(str -> "'" + str + "'")
          .collect(Collectors.joining(","));
      log.debug("{} preNeedJoinedTypeCodes::{}", currUserDtlVo.getUid(), preNeedJoinedTypeCodes);
      // ????????????
      boolean joinedFlag =
          gameModeService.isJoinedOthers(currUserDtlVo.getUid(), activity.getPeriod(),
              preNeedJoinedTypeCodes);
      if (!joinedFlag) {
        dataMap.put("flag", joinedFlag);
        dataMap.put("msg", "?????????????????????");
        return RespDto.succData(dataMap);
      }
    }
    // ??????IP??????
    String ipAddr = IpUtil.getIpAddr(request);
    // ???3???1
    List<PunchTimePeriodVO> vos =
        gameModeService.join(currUserDtlVo.getUid(), activity, amountDecimal, ipAddr, device,
            deviceInfo, locationAlipay);

    PunchTimesPeriodVO vo = this.getPunchTimesPeriodVO(vos, activity.getIs3x1());
    vo.setIsForced(YesOrNoCodes.YES.getCode());
    vo.setIs3x1(activity.getIs3x1().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
        && ActivityUtils.hasRound(activity.getRounds3x1(), vo.getRound()) ? YesOrNoCodes.YES
        .getCode() : YesOrNoCodes.NO.getCode());
    vo.setIsDelay(activity.getIsDelay().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
        && ActivityUtils.hasRound(activity.getDelayRounds(), vo.getRound()) ? YesOrNoCodes.YES
        .getCode() : YesOrNoCodes.NO.getCode());
    boolean flag = (null != vos && !vos.isEmpty());
    dataMap.put("flag", String.valueOf(flag));
    dataMap.put("vo", vo);
    if (!flag) {
      dataMap.put("msg", ErrorCodes.err.getDesc());
    }
    return RespDto.succData(dataMap);
  }

  @RedisRateLimit(limitType = RedisRateLimitType.UID_IP_URI, period = 60, count = 15)
  @ApiOperation(value = "??????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "long", value = "??????ID"),
  })
  @PostMapping("activity/checkin")
  public RespDto activity_checkin(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "") String deviceInfo,
      @RequestParam(required = false, defaultValue = "") String locationAlipay,
      @RequestParam long aid, Device device, HttpServletRequest request) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);

    String flag = gameModeService.checkin(currUserDtlVo.getUid(), LocalDateTime.now(), aid,
        IpUtil.getIpAddr(request), device, deviceInfo, locationAlipay);
    if (flag.equalsIgnoreCase("not_at")) {
      dataMap.put("msg", "??????????????????");
    }
    dataMap.put("flag", flag + "");
    return RespDto.succData(dataMap);
  }

  @RedisRateLimit(limitType = RedisRateLimitType.UID_IP_URI, period = 60, count = 15)
  @ApiOperation(value = "???????????????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "long", value = "??????ID"),
  })
  @PostMapping("activity/next-round")
  public RespDto activity_nextRound(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "") String deviceInfo,
      @RequestParam(required = false, defaultValue = "") String locationAlipay,
      @RequestParam long aid, Device device, HttpServletRequest request) {
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    GameMode activity = gameModeService.getActivityById(aid);
    // ???????????????????????? ??????????????????
    LocalDateTime nowLDT = LocalDateTime.now();
    // ??????????????? ??????????????????
    LocalDateTime stopJoinLdt = activity.getEndDatetime()
        .minusMinutes(null == activity.getDeadlineTimeJoin() ? 150 : activity
            .getDeadlineTimeJoin());
    if (nowLDT.isAfter(stopJoinLdt)) {
      dataMap.put("flag", false);
      dataMap.put("msg", "???????????????????????????");
      return RespDto.succData(dataMap);
    }
    // ?????????????????? ?? ?????????????????????????????????????????????????????????????????????
    if (StringUtils.isNotBlank(activity.getBarredUids())
        && activity.getBarredUids().contains("," + currUserDtlVo.getUid().toString() + ",")) {
      dataMap.put("flag", "false");
      dataMap.put("msg", "???????????????");
      return RespDto.succData(dataMap);
    }

    synchronized (this) {
      // ????????????????????????
      GameModeOrder punchOrder =
          gameModeService.getOrderByActivityId(currUserDtlVo.getUid(), activity.getId(), false);
      //
      if (null == punchOrder) {
        dataMap.put("flag", false);
        dataMap.put("msg", "???????????????");
        return RespDto.succData(dataMap);
      }
      // ?????????????????????
      if (PunchStatusCodes.fail.getCode() == punchOrder.getStatus()) {
        dataMap.put("flag", false);
        dataMap.put("msg", "???????????????????????????????????????");
        return RespDto.succData(dataMap);
      }
      //
      if (PunchStatusCodes.joining.getCode() == punchOrder.getStatus()) {
        dataMap.put("flag", false);
        dataMap.put("msg", "???????????????");
        return RespDto.succData(dataMap);
      }
      // fix ??????????????????????????????????????? ??????????????????????????????
      if (punchOrder.getJoinedRounds() >= punchOrder.getMaxRound()
          || punchOrder.getCurrentRound() >= punchOrder.getMaxRound()) {
        dataMap.put("flag", false);
        dataMap.put("msg", "????????????????????????" + punchOrder.getMaxRound() + "???");
        return RespDto.succData(dataMap);
      }
      //
      List<PunchTimePeriodVO> vos =
          gameModeService.nextRound(currUserDtlVo.getUid(), punchOrder, activity.getEndDatetime(),
              IpUtil.getIpAddr(request), device, deviceInfo, locationAlipay);

      PunchTimesPeriodVO vo = this.getPunchTimesPeriodVO(vos, activity.getIs3x1());
      vo.setIsForced(activity.getIsForced().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
          && ActivityUtils.hasRound(activity.getForcedRounds(), vo.getRound()) ? YesOrNoCodes.YES
          .getCode() : YesOrNoCodes.NO.getCode());
      vo.setIs3x1(activity.getIs3x1().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
          && ActivityUtils.hasRound(activity.getRounds3x1(), vo.getRound()) ? YesOrNoCodes.YES
          .getCode() : YesOrNoCodes.NO.getCode());
      vo.setIsDelay(activity.getIsDelay().equalsIgnoreCase(YesOrNoCodes.YES.getCode())
          && ActivityUtils.hasRound(activity.getDelayRounds(), vo.getRound()) ? YesOrNoCodes.YES
          .getCode() : YesOrNoCodes.NO.getCode());
      boolean flag = (null != vos && !vos.isEmpty());
      dataMap.put("flag", String.valueOf(flag));
      dataMap.put("vo", vo);
      if (!flag) {
        dataMap.put("msg", ErrorCodes.err.getDesc());
      }
      return RespDto.succData(dataMap);
    }
  }

  private PunchTimesPeriodVO getPunchTimesPeriodVO(final List<PunchTimePeriodVO> vos,
      String is3x1) {
    PunchTimesPeriodVO vo = new PunchTimesPeriodVO();
    vo.setIs3x1(is3x1);
    if (vos == null || vos.isEmpty()) {
      return vo;
    }
    vo.setRound(vos.get(0).getRound());
    vo.setTimes(vos.size());

    vo.setStartTime(vos.get(0).getStartTime());
    vo.setEndTime(vos.get(0).getEndTime());

    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(is3x1)) {
      if (vos.size() >= 2) {
        vo.setStartTime2(vos.get(1).getStartTime());
        vo.setEndTime2(vos.get(1).getEndTime());
      }
      if (vos.size() >= 3) {
        vo.setStartTime3(vos.get(2).getStartTime());
        vo.setEndTime3(vos.get(2).getEndTime());
      }
    }
    return vo;
  }

  @Deprecated
  @ApiOperation(value = "???????????????????????????", notes = "Notes")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "aid", dataType = "int", value = "??????ID"),
  })
  @PostMapping("9grid")
  public RespDto get9gridByAid(@RequestAttribute UserDtlVO currUserDtlVo, @RequestParam long aid) {
    GameMode activity = gameModeService.getActivityById(aid);
    List<GameModeGrid9> grid9List =
        gameModeService.getGrid9Data(GameModeActivityTypeCodes.codeOf(activity.getType()));
    return RespDto.succData(grid9List);
  }
}
