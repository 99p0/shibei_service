/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.BrokerageDtl;
import cn.birdplanet.daka.domain.po.BrokerageWithdrawalConditions;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.schedulerx.persistence.punch.BrokerageDtlMapper;
import cn.birdplanet.schedulerx.persistence.punch.BrokerageWithdrawalConditionsMapper;
import cn.birdplanet.schedulerx.persistence.punch.UserMapper;
import cn.birdplanet.schedulerx.service.IBrokerageService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.BrokerageDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: BrokerageDtlServiceImpl
 * @date 2019-07-18 03:18
 */
@Slf4j
@Service
public class BrokerageServiceImpl extends BaseService implements IBrokerageService {

  @Autowired private UserMapper userMapper;
  @Autowired private BrokerageDtlMapper brokerageDtlMapper;
  @Autowired private BrokerageWithdrawalConditionsMapper brokerageWithdrawalConditionsMapper;

  @Override
  public boolean addWithPunch(long uid, long inviteesUid, String inviterNickName, LocalDate period,
      String amount) {
    BrokerageDtl dtl =
        new BrokerageDtl(BrokerageDtlTypeCodes.inviterPunchSucc, uid, inviteesUid,
            "好友“"
                + PunchUtils.checkNickName(inviterNickName)
                + "”"
                + PunchUtils.punchPeriod(period)
                + "期闯关成功的佣金", amount);
    return brokerageDtlMapper.insertSelective(dtl) == 1;
  }

  @Override
  public boolean addWithPunch(long uid, long inviteesUid, String inviterNickName, String title,
      String amount) {
    BrokerageDtl dtl =
        new BrokerageDtl(BrokerageDtlTypeCodes.inviterPunchSucc, uid, inviteesUid,
            "好友“" + PunchUtils.checkNickName(inviterNickName) + "”【" + title + "】打卡佣金", amount);
    return brokerageDtlMapper.insertSelective(dtl) == 1;
  }

  @Override public int turnOffWithdrawal() {
    int count = userMapper.turnOffBrokerageWithdrawal(YesOrNoCodes.NO.getCode());
    return count;
  }

  @Override public synchronized boolean freezeBrokerageForNotEnoughPunchTotal(
      User user, PunchSumVO punchSumVO) {
    // 未达标则扣除所有佣金
    boolean flag = userMapper.freezeBrokerage(user.getUid()) == 1;
    if (flag) {
      brokerageDtlMapper.insertSelective(
          new BrokerageDtl(BrokerageDtlTypeCodes.brokerageFreeze, user.getUid(), "佣金提取未达标，扣除当前所得佣金",
              NumberUtil.format3Str(user.getBrokerage())));
    }
    return flag;
  }

  @Override public boolean turnOffWithdrawal(long uid, YesOrNoCodes yesOrNoCodes) {
    return userMapper.turnOffBrokerageWithdrawalByUid(uid, yesOrNoCodes.getCode()) == 1;
  }

  @Override public BrokerageWithdrawalConditions getCondition() {
    String keys = RedisConstants.BROKERAGE_WITHDRAWAL_CONDITIONS_KEY_PREFIX;
    BrokerageWithdrawalConditions conditions = (BrokerageWithdrawalConditions) redisUtils.get(keys);
    if (null == conditions) {
      Example example = new Example(BrokerageWithdrawalConditions.class);
      conditions = brokerageWithdrawalConditionsMapper.selectOneByExample(example);
      // 加入 redis
      redisUtils.set1Month(keys, conditions);
    }
    return conditions;
  }
}
