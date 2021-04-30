/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.dto.GameModeDTO;
import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.GameModeBenefit;
import cn.birdplanet.daka.domain.po.GameModeBonusMax;
import cn.birdplanet.daka.domain.po.GameModeGear;
import cn.birdplanet.daka.domain.po.GameModeGrid9;
import cn.birdplanet.daka.domain.po.GameModeOrder;
import cn.birdplanet.daka.domain.po.GameModeRound;
import cn.birdplanet.daka.domain.po.GameModeSettleRecord;
import cn.birdplanet.daka.domain.po.GameModeTemplate;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.po.WalletDtl;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.daka.domain.vo.PunchTimePeriodVO;
import cn.birdplanet.daka.infrastructure.commons.util.ActivityUtils;
import cn.birdplanet.daka.infrastructure.commons.util.DeviceUtil;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeBenefitMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeBonusMaxMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeOrderMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeRoundMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeSettleRecordMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.GameModeTemplateMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.Grid9Mapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.RoundMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IBrokerageService;
import cn.birdplanet.daka.infrastructure.service.IGameModeService;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IWalletService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.DozerMapperUtil;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityStatusCodes;
import cn.birdplanet.toolkit.extra.code.GameModeActivityTypeCodes;
import cn.birdplanet.toolkit.extra.code.Gird9IdxCodes;
import cn.birdplanet.toolkit.extra.code.PunchStatusCodes;
import cn.birdplanet.toolkit.extra.code.WalletDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: PunchServiceImpl
 * @date 2019-07-08 09:38
 */
@Slf4j
@Service
public class GameModeServiceImpl extends BaseService implements IGameModeService {

  @Autowired private IUserService userService;
  @Autowired private INoticeService noticeService;
  @Autowired private IBalanceService balanceService;
  @Autowired private IBrokerageService brokerageService;
  @Autowired private IWalletService walletService;

  @Autowired private UserMapper userMapper;
  @Autowired private GameModeBonusMaxMapper gameModeBonusMaxMapper;
  @Autowired private RoundMapper roundMapper;
  @Autowired private Grid9Mapper grid9Mapper;
  @Autowired private GameModeMapper gameModeMapper;
  @Autowired private GameModeTemplateMapper gameModeTemplateMapper;
  @Autowired private GameModeOrderMapper gameModeOrderMapper;
  @Autowired private GameModeRoundMapper gameModeRoundMapper;
  @Autowired private GameModeBenefitMapper gameModeBenefitMapper;
  @Autowired private GameModeSettleRecordMapper gameModeSettleRecordMapper;

  @Override public List<GameModeOrder> getPunchOrdersByActivityId(long activityId) {
    Example example = new Example(GameModeOrder.class);
    example.createCriteria().andEqualTo("activityId", activityId);
    example.orderBy("id").desc();
    return gameModeOrderMapper.selectByExample(example);
  }

  @Override public GameModeOrder getOrderByActivityId(long uid, long activityId, boolean useCache) {
    // todo 查看缓存中是否存在，如果有的话不用去数据库查询 ??  签到失败or成功都需要更新缓存里的信息
    String key = RedisConstants.GAME_MODE_ORDER_KEY_PREFIX + "-u" + uid + "a" + activityId;
    GameModeOrder gameModeOrder = null;
    if (useCache) {
      gameModeOrder = (GameModeOrder) redisUtils.get(key);
    }
    if (null == gameModeOrder) {
      Example example = new Example(GameModeOrder.class);
      example.createCriteria().andEqualTo("activityId", activityId).andEqualTo("uid", uid);
      example.orderBy("id").desc();
      List<GameModeOrder> orders = gameModeOrderMapper.selectByExample(example);
      // 存在打卡失败的情况
      if (orders.size() > 1) {
        for (GameModeOrder order : orders) {
          // 只要打卡状态不为空
          if (order.getStatus() != PunchStatusCodes.fail.getCode()) {
            return order;
          }
        }
      }
      gameModeOrder = orders.isEmpty() ? null : orders.get(0);
      if (null != gameModeOrder) {
        redisUtils.set1Day(key, gameModeOrder);
      }
    }
    return gameModeOrder;
  }

  @Override public List<GameModeGrid9> getGrid9Data(GameModeActivityTypeCodes codes) {
    if (null == codes) { // 使用最低额收益金额
      codes = GameModeActivityTypeCodes.A;
    }
    String keys = RedisConstants.GAME_MODE_GRID9_KEY_PREFIX + codes.getCode();
    List<GameModeGrid9> grid9List = (List<GameModeGrid9>) redisUtils.get(keys);
    if (null == grid9List || grid9List.isEmpty()) {
      Example example = new Example(GameModeGrid9.class);
      example.createCriteria().andEqualTo("status", "Y")
          .andEqualTo("type", codes.getCode());
      example.orderBy("seq").desc().orderBy("amount").desc();
      grid9List = grid9Mapper.selectByExample(example);
      List<GameModeGrid9> finalGrid9List = grid9List;
      taskExecutor.execute(() -> {
        // 加入 redis
        redisUtils.set1Week(keys, finalGrid9List);
      });
    }
    return grid9List;
  }

  @Override public List<GameModeGear> getAllGearDataByType(GameModeActivityTypeCodes code) {
    String key = RedisConstants.GAME_MODE_GEAR_KEY_PREFIX + code.getCode();
    List<GameModeGear> gearList = (List<GameModeGear>) redisUtils.get(key);
    if (null == gearList || gearList.isEmpty()) {
      Example example = new Example(GameModeGear.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("type", code.getCode());
      gearList = roundMapper.selectByExample(example);
      redisUtils.set1Month(key, gearList);
    }
    return gearList;
  }

  private GameModeGrid9 getMaxMultipleGrid9Data(GameModeActivityTypeCodes codes) {
    List<GameModeGrid9> grid9List = this.getGrid9Data(codes);
    GameModeGrid9 maxMultipleData =
        grid9List.stream().max(Comparator.comparingLong(GameModeGrid9::getId)).get();
    return maxMultipleData;
  }

  @Override
  public GameModeRound getCurrPunchRoundsForRedis(final long uid, final long orderId,
      final int currentRound) {
    String roundKey = "gm:rounds:" + uid + ":" + orderId;
    GameModeRound gameModeRound = (GameModeRound) redisUtils.hash_get(roundKey, currentRound);
    if (null == gameModeRound) {
      gameModeRound = this.getCurrPunchRounds(uid, orderId, currentRound);
      if (null != gameModeRound) {
        redisUtils.hash_put1Day(roundKey, currentRound, gameModeRound);
      }
    }
    return gameModeRound;
  }

  @Override public GameModeRound getCurrPunchRounds(long uid, long punchId, int currentRound) {
    Example example = new Example(GameModeRound.class);
    example.createCriteria()
        .andEqualTo("uid", uid)
        .andEqualTo("punchId", punchId)
        .andEqualTo("round", currentRound);
    return gameModeRoundMapper.selectOneByExample(example);
  }

  /**
   * 随机产生[1;2;3], 1出现的概率为15%，2出现的概率为35%，3出现的概率为50%
   *
   * @return
   */
  private int get3x1True() {
    int a = RandomUtils.nextInt(0, 100); //随机产生[0,100)的整数，每个数字出现的概率为1%
    if (a < 15) { // 前15个数字的区间，代表15%的几率
      return 1;
    } else if (a < 50) { // [15,50)，35个数字的区间，代表35%的几率
      return 2;
    } else { // [50,100)，50个数字区间，代表50%的几率
      return 3;
    }
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public synchronized List<PunchTimePeriodVO> join(final long uid, final GameMode activity,
      final BigDecimal amount, String ipAddr, Device device, String deviceInfo,
      String locationAlipay) {
    boolean flag = userMapper.updateBalanceSubtract(uid, amount) == 1;
    if (!flag) {
      throw new RuntimeException("参加打卡更新 余额异常 >>> uid:" + uid + ", amount:" + amount);
    }
    List<PunchTimePeriodVO> vos = Lists.newArrayList();
    int currentRound = 1;
    // 更新 订单， 订单打卡轮次
    GameModeOrder punchOrder = new GameModeOrder();
    punchOrder.setUid(uid);
    punchOrder.setActivityId(activity.getId());
    punchOrder.setPeriod(activity.getPeriod());
    punchOrder.setAmount(amount);
    punchOrder.setCurrentRound(currentRound);
    punchOrder.setJoinedRounds(0);
    punchOrder.setStatus(PunchStatusCodes.joining.getCode());
    punchOrder.setMaxRound(activity.getMaxRound());
    // 设备信息+IP
    punchOrder.setIpAddr(ipAddr);
    punchOrder.setDeviceType(DeviceUtil.getDeviceType(device));
    punchOrder.setDevicePlatform(device.getDevicePlatform().name());
    punchOrder.setDeviceInfo(deviceInfo);
    punchOrder.setLocationAlipay(locationAlipay);
    gameModeOrderMapper.insertSelective(punchOrder);

    GameModeRound punchRound = new GameModeRound();
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIs3x1())
        && ActivityUtils.hasRound(activity.getRounds3x1(), currentRound)) {
      vos =

          this.getPunchTimePeriodFor3x1(currentRound, activity.getEndDatetime(), activity.getType(),
              activity.getDeadlineTimeJoin(), 3);
      punchRound.setRound(currentRound);

      int isTrue = this.get3x1True();
      punchRound.setIsTrue3x1(isTrue);

      punchRound.setStartTime(vos.get(0).getStartTime());
      punchRound.setEndTime(vos.get(0).getEndTime());
      punchRound.setStartTime2(vos.get(1).getStartTime());
      punchRound.setEndTime2(vos.get(1).getEndTime());
      punchRound.setStartTime3(vos.get(2).getStartTime());
      punchRound.setEndTime3(vos.get(2).getEndTime());
    } else {
      // 一轮de打卡的时间段 :: 延时3分钟
      PunchTimePeriodVO punchTimePeriodVO =
          this.getPunchTimePeriod(currentRound, activity.getEndDatetime(), activity.getType(),
              activity.getDeadlineTimeJoin());
      vos.add(punchTimePeriodVO);
      //
      punchRound.setRound(punchTimePeriodVO.getRound());
      punchRound.setIsTrue3x1(1);
      punchRound.setStartTime(punchTimePeriodVO.getStartTime());
      punchRound.setEndTime(punchTimePeriodVO.getEndTime());
    }
    //
    punchRound.setUid(uid);
    punchRound.setActivityId(activity.getId());
    punchRound.setPeriod(activity.getPeriod());
    punchRound.setPunchId(punchOrder.getId());
    punchRound.setStatus(PunchStatusCodes.joining.getCode());
    // 设备信息+IP
    punchRound.setIpAddr(ipAddr);
    punchRound.setDeviceType(DeviceUtil.getDeviceType(device));
    punchRound.setDevicePlatform(device.getDevicePlatform().name());
    punchRound.setDeviceInfo(deviceInfo);
    punchRound.setLocationAlipay(locationAlipay);
    punchRound.setCreatedAt(LocalDateTime.now());
    if (gameModeRoundMapper.insertSelective(punchRound) == 1) {
      // 保存当前轮数
      String roundKey = "rounds:" + uid + ":" + punchOrder.getId();
      redisUtils.hash_put1Day(roundKey, punchRound.getRound(), punchRound);
    }

    // 添加 余额通知
    balanceService.addBalanceDtl(new BalanceDtl(uid, "-" + amount.intValue(),
            "加入"
                + PunchUtils.punchPeriod(activity.getPeriod())
                + "期「"
                + activity.getTitle() + "」"),
        BalanceDtlTypeCodes._join);

    //  活动的总金额，总人数 ++  》》 更新redis里的数据
    gameModeMapper.join(activity.getId(), amount);

    activity.setTotalAmount(activity.getTotalAmount().add(amount));
    activity.setTotalPeople(activity.getTotalPeople() + 1);

    // 更新 闯关列表
    this.getAvailableActivities(this.getCurrPeriod(), false);
    // 更新活动
    String key = RedisConstants.GAME_MODE_ID_KEY_PREFIX + activity.getId();
    redisUtils.set(key, activity, redisUtils.getExpire(key), TimeUnit.SECONDS);
    // 异步处理
    taskExecutor.execute(() -> {
      // 更新用户信息
      userService.changeUserCache(uid);
    });
    return vos;
  }

  /**
   * 组合活动编码
   *
   * @param activity
   * @return
   */
  private String getOrderSn(GameMode activity) {
    return new StringBuilder(20).append("GM")
        .append(activity.getId())
        .append("-")
        .append(activity.getMaxRound())
        .append(activity.getType())
        .toString();
  }

  @Override
  public synchronized List<PunchTimePeriodVO> nextRound(long uid, GameModeOrder punchOrder,
      LocalDateTime activityEndTime, String ipAddr, Device device, String deviceInfo,
      String locationAlipay) {
    GameMode activity = this.getActivityById(punchOrder.getActivityId());
    //
    int nextRound = punchOrder.getCurrentRound() + 1;

    List<PunchTimePeriodVO> vos = Lists.newArrayList();

    GameModeRound punchRound = new GameModeRound();
    if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIs3x1())
        && ActivityUtils.hasRound(activity.getRounds3x1(), nextRound)) {
      vos =
          this.getPunchTimePeriodFor3x1(nextRound, activity.getEndDatetime(), activity.getType(),
              activity.getDeadlineTimeJoin(), 3);

      punchRound.setRound(nextRound);
      int isTure = this.get3x1True();
      punchRound.setIsTrue3x1(isTure);

      punchRound.setStartTime(vos.get(0).getStartTime());
      punchRound.setEndTime(vos.get(0).getEndTime());
      punchRound.setStartTime2(vos.get(1).getStartTime());
      punchRound.setEndTime2(vos.get(1).getEndTime());
      punchRound.setStartTime3(vos.get(2).getStartTime());
      punchRound.setEndTime3(vos.get(2).getEndTime());
    } else {
      // 下一轮的打卡时间段
      PunchTimePeriodVO vo = this.getPunchTimePeriod(nextRound, activityEndTime, activity.getType(),
          activity.getDeadlineTimeJoin());
      vos.add(vo);
      //
      punchRound.setRound(nextRound);
      punchRound.setIsTrue3x1(1);
      punchRound.setStartTime(vo.getStartTime());
      punchRound.setEndTime(vo.getEndTime());
    }

    // 保存当前生成的打卡轮次
    punchRound.setUid(uid);
    punchRound.setActivityId(activity.getId());
    punchRound.setPeriod(activity.getPeriod());
    punchRound.setPunchId(punchOrder.getId());
    punchRound.setStatus(PunchStatusCodes.joining.getCode());
    // 设备信息+IP
    punchRound.setIpAddr(ipAddr);
    punchRound.setDeviceType(DeviceUtil.getDeviceType(device));
    punchRound.setDevicePlatform(device.getDevicePlatform().name());
    punchRound.setDeviceInfo(deviceInfo);
    punchRound.setLocationAlipay(locationAlipay);
    punchRound.setCreatedAt(LocalDateTime.now());
    if (gameModeRoundMapper.insertSelective(punchRound) == 1) {
      // 保存当前轮数
      String roundKey = "rounds:" + uid + ":" + punchOrder.getId();
      redisUtils.hash_put1Day(roundKey, punchRound.getRound(), punchRound);
    }
    // 当前轮的打卡的轮
    gameModeOrderMapper.updateNextRoundById(punchOrder.getId(), uid);
    return vos;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public synchronized String checkin(long uid, LocalDateTime punchTime, long aid, String ipAddr,
      Device device, String deviceInfo, String locationAlipay) {
    GameMode activity = this.getActivityById(aid);
    // 已参加多少轮闯关
    GameModeOrder punchOrder = this.getOrderByActivityId(uid, activity.getId(), false);
    log.debug("punchOrder111::{}", punchOrder);
    String flag = this.checkin(uid, activity, punchOrder, punchTime, ipAddr, device, deviceInfo,
        locationAlipay);
    // 签到成功，是否强制打卡， 当前签到的轮数
    if ("true".equalsIgnoreCase(flag)) {
      // 查看下一轮是否强制 :: 签到成功后 当前轮
      final int nextRound = punchOrder.getCurrentRound() + 1;
      log.debug("IsForced:: {} MRounds::{} nextRound::{}", activity.getIsForced(),
          activity.getForcedRounds(), nextRound);
      if (StringUtils.isNotBlank(activity.getIsForced())
          && YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsForced())
          && ActivityUtils.hasRound(activity.getForcedRounds(), nextRound)) {
        // 重新获取加载后的数据 ?? 更新数据， 不用入库查询
        log.debug("MRounds 存在 进行强制次轮签到 ::{}", punchOrder);
        List<PunchTimePeriodVO> vos =
            this.nextRound(uid, punchOrder, activity.getEndDatetime(), ipAddr, device, deviceInfo,
                locationAlipay);
        log.debug("强制下一轮：{}/{} >{}", uid, aid, vos);
      }
    }
    return flag;
  }

  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public synchronized String checkin(long uid, GameMode activity, GameModeOrder punchOrder,
      LocalDateTime punchTime, String ipAddr, Device device, String deviceInfo,
      String locationAlipay) {

    boolean flag = false;
    int a, b;
    // 获取当前打卡的轮次
    GameModeRound punchRound =
        this.getCurrPunchRounds(uid, punchOrder.getId(), punchOrder.getCurrentRound());

    // 以下处理必须是真是的
    LocalDateTime startTimeTrue = punchRound.getStartTime();
    LocalDateTime endTimeTrue = punchRound.getEndTime();

    if (StringUtils.isNotBlank(activity.getIs3x1())
        && YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIs3x1())) {
      // 第二个为真
      if (punchRound.getIsTrue3x1() == 2) {
        startTimeTrue = punchRound.getStartTime2();
        endTimeTrue = punchRound.getEndTime2();
      } else if (punchRound.getIsTrue3x1() == 3) {
        // 第三个为真
        startTimeTrue = punchRound.getStartTime3();
        endTimeTrue = punchRound.getEndTime3();
      }
    }

    // 当前轮已经打卡
    if (punchRound.getStatus() != 1 && null != punchRound.getCheckinTime()) {
      if (punchRound.getStatus() == 2) {
        return true + ""; // 成功打卡
      } else if (punchRound.getStatus() == 3) {
        return false + ""; // 错过打卡
      }
    }
    if (punchTime.isBefore(startTimeTrue)) {
      return "not_at";
    }
    // 在规定时间内
    if (punchTime.isAfter(startTimeTrue)
        && punchTime.isBefore(endTimeTrue)) {
      // 更新打卡轮次
      a = gameModeRoundMapper.updateCheckinStatusSucc(uid, punchRound.getId(),
          PunchStatusCodes.success.getCode(), ipAddr, DeviceUtil.getDeviceType(device),
          device.getDevicePlatform().name(), deviceInfo, locationAlipay);
      // 更新打卡
      b = gameModeOrderMapper.updatePunchStatusSuccess(uid, punchOrder.getId());
      if (a == b && a == 1) {
        flag = true;
        //taskExecutor.execute(() -> {
        // 系统通知
        noticeService.addPunchResultNoticeForGM(uid, activity, punchRound.getRound(), true,
            PunchUtils.buildCheckinTimeForNotice(startTimeTrue,
                endTimeTrue));
        //});
      } else {
        throw new RuntimeException("checkin 更新数据异常");
      }
    } else if (punchTime.isAfter(endTimeTrue)) {
      a = gameModeRoundMapper.updateCheckinStatus(uid, punchRound.getId(),
          PunchStatusCodes.fail.getCode());
      b = gameModeOrderMapper.updatePunchStatus(uid, punchOrder.getId(),
          PunchStatusCodes.fail.getCode());
      // 活动 失败的总金额， 失败的总人数
      gameModeMapper.updateWhitCheckinFailById(activity.getId(), punchOrder.getAmount());
      if (a == b && a == 1) {
        flag = false;
        //taskExecutor.execute(() -> {
        // 系统通知
        noticeService.addPunchResultNoticeForGM(uid, activity, punchRound.getRound(),
            false, PunchUtils.buildCheckinTimeForNotice(startTimeTrue,
                endTimeTrue));
        //});
      } else {
        throw new RuntimeException("checkin 更新数据异常");
      }
    }
    return flag + "";
  }

  @Override public GameModeRound getPunchRoundByPunchOrder(long uid, long id, int currentRound) {
    Example example = new Example(GameModeRound.class);
    example.createCriteria()
        .andEqualTo("uid", uid)
        .andEqualTo("punchId", id)
        .andEqualTo("round", currentRound);
    return gameModeRoundMapper.selectOneByExample(example);
  }

  /**
   * 获取打卡的时间段 六点之后可以打卡
   *
   * @param round 轮次
   */
  private PunchTimePeriodVO getPunchTimePeriod(int round, LocalDateTime activityEndTime,
      String type, int deadlineTimeJoin) {
    // 打卡的时间段
    LocalDateTime checkinStartTime, checkinEndTime, magicalTime,
        // 当前时间  2019-09-17 06:45:00
        currLdt = LocalDateTime.now();
    // 如果当前时间大于，截止时间
    if (currLdt.isAfter(activityEndTime)) {
      throw new RuntimeException("活动已截止，不能再在参加了");
    }

    // !!! 距离结束的分钟数
    int lastMin = (int) (PunchUtils.getPunchingSeconds(currLdt, activityEndTime) / 60);
    log.debug("距离活动截止的分钟数:{}", lastMin);
    // 如果当前时间在结束前的一个打卡周期150m，禁止打卡
    if (lastMin <= deadlineTimeJoin) {
      throw new RuntimeException("此时间段禁止参加打卡");
    }
    // 每轮打卡相关的参数信息
    GameModeGear punchGear = this.getGearByRound(round, type);
    //
    if (lastMin <= (punchGear.getRangeMax() / 60)) {
      throw new RuntimeException("此时间段不满足打卡条件");
    }
    // 随机范围: 活动截止前的时候小于签到周期的话， 则按距离活动结束的秒数计算
    int range = RandomUtils.nextInt(punchGear.getRangeMin(), punchGear.getRangeMax());
    log.debug("随机数:{}", range);

    // 签到的开始时间
    magicalTime = currLdt.plusSeconds(range);
    // 在 5～58 中随机一个
    LocalTime localTime =
        LocalTime.of(magicalTime.toLocalTime().getHour(), magicalTime.toLocalTime().getMinute(),
            currLdt.getSecond());
    checkinStartTime = LocalDateTime.of(magicalTime.toLocalDate(), localTime);
    // 签到结束时间 = 随机的开始时间+ 每一轮登记需要额的时间 :: 更改单位为：秒
    checkinEndTime = checkinStartTime.plusSeconds(punchGear.getTimePeriod());
    log.debug("随机数: {}, 签到时间段:{} ～ {}", range, checkinStartTime, checkinEndTime);
    return new PunchTimePeriodVO(round, checkinStartTime, checkinEndTime);
  }

  /**
   * 获取打卡的时间段 六点之后可以打卡
   *
   * @param round 轮次
   */
  private List<PunchTimePeriodVO> getPunchTimePeriodFor3x1(int round, LocalDateTime activityEndTime,
      String type, int deadlineTimeJoin, int _3x1) {
    List<PunchTimePeriodVO> list = Lists.newArrayList();
    // 打卡的时间段
    LocalDateTime currLdt = LocalDateTime.now();
    // 如果当前时间大于，截止时间
    if (currLdt.isAfter(activityEndTime)) {
      throw new RuntimeException("活动已截止，不能再在参加了");
    }
    // !!! 距离结束的分钟数
    int lastMin = (int) (PunchUtils.getPunchingSeconds(currLdt, activityEndTime) / 60);
    log.debug("距离活动截止的分钟数:{}", lastMin);
    // 如果当前时间在结束前的一个打卡周期150m，禁止打卡
    if (lastMin <= deadlineTimeJoin) {
      throw new RuntimeException("此时间段禁止参加打卡");
    }
    // 每轮打卡相关的参数信息
    GameModeGear punchGear = this.getGearByRound(round, type);
    //
    if (lastMin <= (punchGear.getRangeMax() / 60)) {
      throw new RuntimeException("此时间段不满足打卡条件");
    }

    // 每个延迟时间的周期不一样
    int timePeriod = punchGear.getTimePeriod();
    String timePeriods3x1 =
        StringUtils.isNotBlank(punchGear.getTimePeriods3x1()) ? punchGear.getTimePeriods3x1()
            : "";
    String[] timePeriodArr = timePeriods3x1.split(",");

    // 计算周期：
    int checkinRangeSpilt = (punchGear.getRangeMax() - punchGear.getRangeMin()) / _3x1;
    int startInclusive = 0, endExclusive = 0, range = 0, second;
    PunchTimePeriodVO timePeriodVO;
    LocalTime localTime;
    LocalDateTime checkinStartTime, checkinEndTime, magicalTime, lastCheckinEndTime = currLdt;
    // 3x1 : 第一轮：随机时间/2， 抽取一个随机数， 进行分配第一轮， 第二轮，在第一轮的结束时间后 随机一个数， 第三轮在第二轮的结束时间随机一个数
    for (int i = 0, j; i < _3x1; i++) {
      j = i + 1;

      startInclusive = punchGear.getRangeMin() + (checkinRangeSpilt * i);
      endExclusive = punchGear.getRangeMin() + (checkinRangeSpilt * j);
      range = RandomUtils.nextInt(startInclusive, endExclusive);
      // 签到的开始时间:
      magicalTime = currLdt.plusSeconds(range);
      // 在 5～58 中随机一个
      localTime =
          LocalTime.of(magicalTime.toLocalTime().getHour(), magicalTime.toLocalTime().getMinute(),
              currLdt.getSecond());
      checkinStartTime = LocalDateTime.of(magicalTime.toLocalDate(), localTime);
      try {
        timePeriod = Integer.parseInt(timePeriodArr[i]);
      } catch (Exception e) {
        log.error("延时签到时间设置错误:{}，默认为{}; err:{}", timePeriodArr, timePeriod, e.getMessage());
        punchGear.getTimePeriod();
      }
      checkinEndTime = checkinStartTime.plusSeconds(timePeriod);
      // 计算下一轮
      lastCheckinEndTime = checkinEndTime;
      log.debug("{}/{}轮,随机数:{}, 签到时间段:{}～{}", j, round, range, checkinStartTime,
          checkinEndTime);
      timePeriodVO = new PunchTimePeriodVO(round, j, checkinStartTime, checkinEndTime);
      list.add(timePeriodVO);
    }
    return list;
  }

  /**
   * 获取第几轮次 需要的打卡时间分钟
   */
  private GameModeGear getGearByRound(int round, String type) {

    String key = RedisConstants.GAME_MODE_GEAR_KEY_PREFIX + type + ":" + round;
    GameModeGear punchGear = (GameModeGear) redisUtils.get(key);
    if (null == punchGear) {
      Example example = new Example(GameModeGear.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("round", round).andEqualTo("type", type);
      punchGear = roundMapper.selectOneByExample(example);
      if (null != punchGear) {
        redisUtils.set1Month(key, punchGear);
      }
    }
    return punchGear;
  }

  @Override public LocalDate getCurrPeriod() {
    LocalDateTime currLdt = LocalDateTime.now();
    // 如果时间是八点之前  获取 前一天的
    if (currLdt.getHour() < 8) {
      currLdt = currLdt.minusDays(1);
    }
    return currLdt.toLocalDate();
  }

  private String getCurrPeriodForRedisKey(LocalDate localDate) {
    return RedisConstants.GAME_MODE_KEY_PREFIX + PunchUtils.punchPeriod(localDate);
  }

  @Override public GameMode getCurrMainActivity() {
    LocalDate currLdt = this.getCurrPeriod();
    String key = this.getCurrPeriodForRedisKey(currLdt);
    log.debug("当前活动的日期：{}", currLdt);
    GameMode gameMode = (GameMode) redisUtils.get(key);
    if (null == gameMode) {
      gameMode = this.getCurrActivityByPeriodWithType(currLdt, GameModeActivityTypeCodes.A);
      if (null == gameMode) {
        log.error("gameMode 数据异常:无{}日期的任务", currLdt);
      } else {
        redisUtils.set2Day(key, gameMode);
      }
    }
    return gameMode;
  }

  private List<GameModeBonusMax> getAllGameModeBonus(GameModeActivityTypeCodes codes) {
    String keys = RedisConstants.GAME_MODE_BONUS_LIST_KEY_PREFIX + codes.getCode();
    List<GameModeBonusMax> bonusList = (List<GameModeBonusMax>) redisUtils.get(keys);
    if (null == bonusList || bonusList.isEmpty()) {
      Example example = new Example(GameModeBonusMax.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("type", codes.getCode()).andEqualTo("status", YesOrNoCodes.YES.getCode());
      bonusList = gameModeBonusMaxMapper.selectByExample(example);
      if (!bonusList.isEmpty()) {
        // 存入redis
        List<GameModeBonusMax> finalBonusList = bonusList;
        taskExecutor.execute(() -> redisUtils.set1Month(keys, finalBonusList));
      }
    }
    return bonusList;
  }

  private BigDecimal getGameModeBonusMaxRandom(GameModeActivityTypeCodes codes) {
    List<GameModeBonusMax> bonusList = this.getAllGameModeBonus(codes);
    // 随机排序
    Collections.shuffle(bonusList);
    BigDecimal bonus;
    try {
      bonus = bonusList.get(0).getBonus();
    } catch (Exception e) {
      log.error("ERR 随机的奖金", e);
      bonus = new BigDecimal("13.50");
    }
    log.info("随机的奖金:{}", bonus);
    return bonus;
  }

  @Override public GameMode getCurrActivityByPeriodWithType(LocalDate period,
      GameModeActivityTypeCodes code) {
    Example example = new Example(GameMode.class);
    // 使用字符串进行日期的比对
    example.createCriteria().andCondition("period = '" + period + "'")
        .andEqualTo("type", code.getCode())
        .andEqualTo("status", GameModeActivityStatusCodes.normal.getCode());
    GameMode activity = gameModeMapper.selectOneByExample(example);

    return activity;
  }

  @Override public List<GameMode> getActivitiesByPeriod(LocalDate period) {
    Example example = new Example(GameMode.class);
    // 使用字符串进行日期的比对
    example.createCriteria().andCondition("period = '" + period + "'");
    List<GameMode> list = gameModeMapper.selectByExample(example);
    return list;
  }

  @Override public GameMode getActivityById(long aid) {
    String key = RedisConstants.GAME_MODE_ID_KEY_PREFIX + aid;
    GameMode activity = (GameMode) redisUtils.get(key);
    if (null == activity) {
      activity = gameModeMapper.selectByPrimaryKey(aid);
      if (null != activity) {
        redisUtils.set2Day(key, activity);
      }
    }
    return activity;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean settle(final GameMode activity) {
    log.info("结算活动:{}", activity);
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加的人数:{}", orders.size());
    // 获取签到成功订单
    List<GameModeOrder> succOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.success.getCode())
        .collect(Collectors.toList());
    log.info("打卡成功的人数:{}", succOrders.size());
    // 失败金 = (总人数-失败人数)* 奖励基数
    BigDecimal _300_9 =
        this.getGameModeBonusMaxRandom(GameModeActivityTypeCodes.codeOf(activity.getType()));
    int succPeople = activity.getTotalPeople() - activity.getFailTotalPeople();
    BigDecimal bonusPool = _300_9.multiply(new BigDecimal(succPeople)),
        bonusPoolReal = BirdplanetConstants.ZERO_BD;
    log.info("失败金:{}", bonusPool);

    BigDecimal maxMultiple =
        this.getMaxMultipleGrid9Data(GameModeActivityTypeCodes.codeOf(activity.getType()))
            .getAmount();
    log.info(".getGameModeBonusMaxRandom:{}", maxMultiple);
    // 最小粒度的奖金 : 成功人数*支付的金额/最大倍数/最大轮数）, 小数点之后 直接舍弃
    BigDecimal miniBonus = (succOrders.size() == 0) ? new BigDecimal("0.00") :
        bonusPool.divide(
            (maxMultiple.multiply(new BigDecimal(activity.getMaxRound() * succOrders.size()))),
            6, BigDecimal.ROUND_DOWN);
    log.info("{}期{}/人均最小额度的奖金:{}", activity.getId(), PunchUtils.punchPeriod(activity.getPeriod()),
        miniBonus);

    // 分钱
    for (GameModeOrder order : orders) {
      GameModeSettleRecord settleRecord = new GameModeSettleRecord(order);
      // 防止数据异常的
      if (order.getStatus() == PunchStatusCodes.success.getCode()) {
        // 当前的用户信息
        User user = userService.getByUid(order.getUid());
        // 奖金 = 最小奖金*参加的轮次{大于最大轮数则按最大轮处理}*倍数
        BigDecimal bonus = order.getAmount()
            .multiply(new BigDecimal(
                order.getJoinedRounds() <= activity.getMaxRound() ? order.getJoinedRounds()
                    : activity.getMaxRound()))
            .multiply(miniBonus);
        log.info("uid:{} 奖金：{}", order.getUid(), bonus);
        // 计算实际的奖金发放
        bonusPoolReal = bonusPoolReal.add(bonus);
        // 是否存在邀请人
        if (StringUtils.isNotBlank(user.getInviterCode())) {
          // 不为空，且佣金关系为Y的时候 结算佣金
          if (StringUtils.isNotBlank(activity.getIsSettleCommission()) &&
              YesOrNoCodes.YES.getCode().equalsIgnoreCase(activity.getIsSettleCommission())) {

            log.info("uid:{} 存在邀请人 uid:{}, code:{}", order.getUid(), user.getInviterUid(),
                user.getInvitationCode());
            // 邀请用户参加闯关赢取奖金部分的10%作为佣金奖励。
            BigDecimal inviterBrokerage = bonus.multiply(new BigDecimal("0.1"));
            log.info("uid:{} 邀请人的佣金：{}", order.getUid(), inviterBrokerage);
            //佣金++ , 佣金总额++
            userMapper.updateBrokerageAddAndTotalBrokerageAdd(user.getInviterUid(),
                inviterBrokerage);
            //
            settleRecord.setInviterBrokerage(inviterBrokerage);
            // 添加佣金记录
            brokerageService.addWithPunch(user.getInviterUid(), user.getUid(),
                user.getNickName(), order.getPeriod(), NumberUtil.format3Str(inviterBrokerage));

            // 扣除邀请人的佣金部分
            bonus = bonus.subtract(inviterBrokerage);
            log.info("uid:{} 扣除佣金后的奖金：{}", order.getUid(), bonus);
          }
        }
        // 更新用户的钱包
        BigDecimal principalAndBonus = order.getAmount().add(bonus);
        log.info("uid:{} 本金+奖金：{}", order.getUid(), principalAndBonus);
        if (userMapper.updateWalletAdd(user.getUid(), principalAndBonus) == 1) {
          log.info("uid:{} 已发放至钱包：{}", order.getUid(), principalAndBonus);
        } else {
          log.info("uid:{} 更新钱包失败：{}", order.getUid(), principalAndBonus);
        }
        // 钱包增加记录
        WalletDtl walletDtl =
            new WalletDtl(user.getUid(), "+" + NumberUtil.format3Str(principalAndBonus),
                new StringBuffer().append(PunchUtils.punchPeriod(order.getPeriod()))
                    .append("期「")
                    .append(activity.getTitle())
                    .append("」")
                    .append(WalletDtlTypeCodes._punch.getDesc())
                    .toString());
        walletService.addWalletDtl(walletDtl, WalletDtlTypeCodes._punch);
        //
        settleRecord.setBonus(bonus);
      } else {
        //
        settleRecord.setBonus(BirdplanetConstants.ZERO_BD);
        settleRecord.setInviterBrokerage(BirdplanetConstants.ZERO_BD);
      }
      // 发放额外奖励
      List<GameModeBenefit> benefits =
          this.getAllAvailableBenefits(GameModeActivityTypeCodes.codeOf(activity.getType()));
      BigDecimal benefitCount = BirdplanetConstants.ZERO_BD;
      if (!benefits.isEmpty()) {
        for (GameModeBenefit benefit : benefits) {
          // 金额一直 且 打卡的轮数要大于等于规定的轮数
          if (benefit.getPunchAmount().compareTo(order.getAmount()) == 0
              && order.getJoinedRounds().intValue() >= benefit.getPunchRound().intValue()) {
            // 需要打卡成功，进行奖励
            if (YesOrNoCodes.YES.getCode().equalsIgnoreCase(benefit.getNeedPunchSucc())) {
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            } else { // 不需要成功打卡，进行奖励
              this.settleBenefits(order, benefit);
              benefitCount = benefitCount.add(benefit.getBonus());
            }
          }
        }
      }
      //
      settleRecord.setBenefits(benefitCount);
      // 结算记录数据库
      gameModeSettleRecordMapper.insertSelective(settleRecord);
      // 计算真实的分配奖金
      bonusPoolReal = bonusPoolReal.add(benefitCount);
    }
    // 结算后更改状态
    return gameModeMapper.updateActivitySettleComplete(activity.getId(), bonusPool, bonusPoolReal)
        == 1;
  }

  @Override public boolean updateActivityComplete(long aid) {
    return gameModeMapper.updateActivityStatus(aid, GameModeActivityStatusCodes.finish.getCode())
        == 1;
  }

  @Override public boolean updateActivitySettle(long aid) {
    return gameModeMapper.updateActivityStatus(aid, GameModeActivityStatusCodes.settle.getCode())
        == 1;
  }

  @Override public List<PunchSumVO> getPunchSumByMonth(LocalDate firstDay,
      LocalDate lastDay, int joinedRoundsSum) {
    return gameModeOrderMapper.getPunchSumByMonth_gt(firstDay, lastDay, joinedRoundsSum);
  }

  @Override public List<PunchSumVO> getPunchSumByMonthForFreeze(LocalDate firstDay,
      LocalDate lastDay, int joinedRoundsSum) {
    return gameModeOrderMapper.getPunchSumByMonth_lt(firstDay, lastDay, joinedRoundsSum);
  }

  @Override public GameModeDTO gameMode2Dto(GameMode record) {
    GameModeDTO dto = DozerMapperUtil.map(record, GameModeDTO.class);
    //
    dto.setTotalAmount(record.getTotalAmount().add(record.getDummyTotalAmount()));
    dto.setTotalPeople(record.getTotalPeople() + record.getDummyTotalPeople());
    return dto;
  }

  @Override
  public List<GameModeDTO> getAvailableActivities(LocalDate currPeriod, boolean redisCache) {
    String key = RedisConstants.GAME_MODE_LIST_KEY_PREFIX + currPeriod;
    List<GameModeDTO> activities = null;
    if (redisCache) {
      try {
        activities = (List<GameModeDTO>) redisUtils.get(key);
      } catch (Exception e) {
        log.error("获取数据异常清空：{}", key);
        redisUtils.del(key);
      }
    }
    if (null == activities) {
      Example example = new Example(GameMode.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("period", currPeriod)
          .andEqualTo("status", GameModeActivityStatusCodes.normal.getCode());
      List<GameMode> data = gameModeMapper.selectByExample(example);
      if (!data.isEmpty()) {
        // 将 gameMode 转化成Dto
        activities = Lists.newArrayListWithExpectedSize(data.size());
        GameModeDTO dto;
        for (GameMode record : data) {
          dto = this.gameMode2Dto(record);
          activities.add(dto);
        }
        //
        List<GameModeDTO> modeList;
        // 每天早上覆盖之前的打卡列表数据
        redisUtils.set2Day(key, activities);
        // 常规: 即不是回血房，不是延迟， 不是一分钟，
        modeList = activities.stream()
            .filter(gameMode -> gameMode.getGird9Idx() == Gird9IdxCodes.G20.getCode())
            .collect(Collectors.toList());
        redisUtils.set2Day(key + "cg", modeList);
        // 回血房
        modeList = activities.stream()
            .filter(gameMode -> gameMode.getGird9Idx() == Gird9IdxCodes.G40.getCode())
            .collect(Collectors.toList());
        redisUtils.set2Day(key + "hx", modeList);
        // 延迟
        modeList = activities.stream()
            .filter(gameMode -> gameMode.getGird9Idx() == Gird9IdxCodes.G10.getCode())
            .collect(Collectors.toList());
        redisUtils.set2Day(key + "yc", modeList);
        // 三选一
        modeList = activities.stream()
            .filter(gameMode -> gameMode.getGird9Idx() == Gird9IdxCodes.G30.getCode())
            .collect(Collectors.toList());
        redisUtils.set2Day(key + "sy", modeList);
        // 一分钟
        modeList = activities.stream()
            .filter(gameMode -> gameMode.getGird9Idx() == Gird9IdxCodes.G1.getCode())
            .collect(Collectors.toList());
        redisUtils.set2Day(key + "om", modeList);
      } else {
        // 防止缓存穿透
        redisUtils.setMinutes(key, activities, 1);
      }
    }
    return activities;
  }

  @Override public synchronized boolean generateNextActivityWithTemplate(LocalDate period) {
    Example example = new Example(GameModeTemplate.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("status", "Y");
    List<GameModeTemplate> templates = gameModeTemplateMapper.selectByExample(example);
    if (templates.isEmpty()) {
      log.info("没有查询到闯关的模版任务");
      return true;
    }
    // 根据模版生成新的闯关
    templates.forEach(gameModeTemplate -> {
      log.debug("根据模版生成新的闯关 template:{}", gameModeTemplate);
      GameMode activity = new GameMode(gameModeTemplate, period);
      boolean flag = gameModeMapper.insertSelective(activity) == 1;
      log.debug("根据模版生成新的闯关 activity:{}", activity);
      if (flag) {
        taskExecutor.execute(() -> {
          //
          redisUtils.set2Day(RedisConstants.GAME_MODE_ID_KEY_PREFIX + activity.getId(), activity);
          // 更新 模版的last_period
          gameModeTemplateMapper.updateLastPeriodById(gameModeTemplate.getId(), period);
        });
      }
    });
    return false;
  }

  @Override public void statisticsByMonth(String y_month) {
    // todo 统计
  }

  @Override public boolean isJoinedOthers(long uid, LocalDate period,
      GameModeActivityTypeCodes typeCodes) {
    int joinedNum = gameModeOrderMapper.getJoinedNumByCondtion(uid, period, typeCodes.getCode());
    return joinedNum > 0;
  }

  @Override public boolean isJoinedOthers(long uid, LocalDate period, String typeCodes) {
    int joinedNum = gameModeOrderMapper.getJoinedNumByUidAndCodes(uid, period, typeCodes);
    return joinedNum > 0;
  }

  private void settleBenefits(final GameModeOrder order, final GameModeBenefit benefit) {
    // 余额增加金额 & 添加一条记录
    log.info("uid:{} {}倍{}轮：额外奖励{}", order.getUid(), benefit.getPunchAmount(),
        benefit.getPunchRound(), benefit.getBonus());
    if (userMapper.updateBalanceAdd(order.getUid(), benefit.getBonus()) == 1) {
      log.info("uid:{} 额外奖励已发放至余额：{}", order.getUid());
    } else {
      log.info("uid:{} 额外奖励更新钱包失败：{}", order.getUid());
    }
    // 余额增加记录
    BalanceDtl benefitBalanceDtl =
        new BalanceDtl(order.getUid(), "+" + NumberUtil.format3Str(benefit.getBonus()),
            this.buildBenefitDesc(order.getPeriod(), benefit.getPunchAmount(),
                benefit.getPunchRound().intValue()));
    balanceService.addBalanceDtl(benefitBalanceDtl, BalanceDtlTypeCodes._benefit);
  }

  private String buildBenefitDesc(LocalDate period, BigDecimal punchAmount, int punchRound) {
    return new StringBuilder(PunchUtils.punchPeriod(period)).append("期")
        .append(punchAmount.intValue())
        .append("倍")
        .append(punchRound)
        .append("轮")
        .append(WalletDtlTypeCodes._benefit.getDesc())
        .toString();
  }

  @Override public List<GameModeBenefit> getAllAvailableBenefits(GameModeActivityTypeCodes codes) {
    String keys = RedisConstants.GAME_MODE_BENEFIT_KEY_PREFIX + codes.getCode();
    List<GameModeBenefit> benefits = (List<GameModeBenefit>) redisUtils.get(keys);
    if (null == benefits || benefits.isEmpty()) {
      Example example = new Example(GameModeBenefit.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("status", "Y").andEqualTo("type", codes.getCode());
      benefits = gameModeBenefitMapper.selectByExample(example);
      redisUtils.set1Month(keys, benefits);
    }
    return benefits;
  }

  @Override public List<GameMode> getAllByPage(int pageNum, int pageSize) {
    Example example = new Example(GameMode.class);
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return gameModeMapper.selectByExample(example);
  }

  @Override public synchronized boolean triggerPunchFailedByActivity(final GameMode activity) {
    // 获取参加打卡的订单
    List<GameModeOrder> orders = this.getPunchOrdersByActivityId(activity.getId());
    log.info("参加{}期闯关的人数:{}", activity.getPeriod(), orders.size());
    //  有部分用户是参加了打卡，但是没有进入系统， 所以在活动结束之后 统一触发打卡失败的
    List<GameModeOrder> joiningOrders = orders.parallelStream()
        .filter(punchOrder -> punchOrder.getStatus() == PunchStatusCodes.joining.getCode())
        .collect(Collectors.toList());
    log.info("打卡中的人数:{}", joiningOrders.size());
    // 触发失败
    if (!joiningOrders.isEmpty()) {
      joiningOrders.forEach(punchOrder -> {
        // 查询当前轮
        GameModeRound punchRound =
            this.getPunchRoundByPunchOrder(punchOrder.getUid(), punchOrder.getId(),
                punchOrder.getCurrentRound());
        // fix 失败
        gameModeRoundMapper.updateCheckinStatus(punchOrder.getUid(), punchRound.getId(),
            PunchStatusCodes.fail.getCode());
        // fix 失败
        gameModeOrderMapper.updatePunchStatus(punchOrder.getUid(), punchOrder.getId(),
            PunchStatusCodes.fail.getCode());
        // 活动 失败的总金额， 失败的总人数
        gameModeMapper.updateWhitCheckinFailById(activity.getId(), punchOrder.getAmount());
        // 系统通知
        noticeService.addPunchResultNotice(punchOrder.getUid(), activity.getPeriod(),
            punchRound.getRound(), false,
            PunchUtils.buildCheckinTimeForNotice(punchRound.getStartTime(),
                punchRound.getEndTime()));
      });
    }
    return true;
  }
}
