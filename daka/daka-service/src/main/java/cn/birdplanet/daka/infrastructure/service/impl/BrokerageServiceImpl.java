/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.BrokerageDtl;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.ActionVo;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.daka.infrastructure.persistence.punch.BrokerageDtlMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IBrokerageService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.core.math.NumberUtil;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.BrokerageDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import com.github.pagehelper.PageHelper;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired private IBalanceService balanceService;
  @Autowired private IUserService userService;
  @Autowired private UserMapper userMapper;
  @Autowired private BrokerageDtlMapper brokerageDtlMapper;

  //@Cacheable(value = "brokerageDtl", key = "#uid + '_' + #pageNum+ '_' + #pageSize")
  @Override
  public List<BrokerageDtl> getByUidWithPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(BrokerageDtl.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return brokerageDtlMapper.selectByExample(example);
  }

  @Override public List<BrokerageDtl> getAllWithPage(int pageNum, int pageSize) {

    return this.getByUidWithPage(pageNum, pageSize, 0L);
  }

  @Override
  public synchronized ActionVo settle(long uid) {
    User user = userService.getByUid(uid);
    int brokerageIntValue = user.getBrokerage().intValue();
    //
    if(YesOrNoCodes.NO.getCode().equalsIgnoreCase(user.getBrokerageWithdrawalSwitch())){
      return new ActionVo(false, "未满足佣金提取条件");
    }
    // 防止为负数
    if (brokerageIntValue < 1) {
      return new ActionVo(false, "请稍后重试");
    }
    boolean flag = userMapper.updateBalanceAddAndBrokerageSub(uid, brokerageIntValue) == 1;
    if (flag) {
      // 佣金减少通知
      brokerageDtlMapper.insertSelective(
          new BrokerageDtl(BrokerageDtlTypeCodes.settleToBalance, uid, "结算佣金到余额",
              brokerageIntValue + ".000"));
      // 余额增加通知
      balanceService.addBalanceDtl(new BalanceDtl(uid, "+" + brokerageIntValue, "提取佣金"),
          BalanceDtlTypeCodes._settleBrokerage);
    }
    return new ActionVo(flag, "");
  }

  @Override
  public boolean dtlRead(long uid, long dtlId) {
    return brokerageDtlMapper.updateDtlRead(uid, dtlId) == 1;
  }

  @Override
  public boolean addWithPunch(long uid, long inviteesUid, String inviterNickName, LocalDate period,
      String amount) {
    BrokerageDtl dtl =
        new BrokerageDtl(BrokerageDtlTypeCodes.inviterPunchSucc, uid, inviteesUid,
            "好友“"
                + this.checkNickName(inviterNickName)
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
            "好友“" + this.checkNickName(inviterNickName) + "”【" + title + "】打卡佣金", amount);
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

  private String checkNickName(String nickName) {
    return StringUtils.isNotBlank(nickName) ? nickName : "未设置昵称";
  }
}
