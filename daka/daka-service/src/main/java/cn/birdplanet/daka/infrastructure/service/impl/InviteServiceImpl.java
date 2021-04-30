/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.Invite;
import cn.birdplanet.daka.domain.po.ServiceFuwu;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.persistence.punch.InviteMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.ServiceFuwuMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IInviteService;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.FuwuTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.github.pagehelper.PageHelper;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: InviteServiceImpl
 * @date 2019-07-19 15:33
 */
@Slf4j
@Service
public class InviteServiceImpl extends BaseService implements IInviteService {

  @Autowired private INoticeService noticeService;
  @Autowired private InviteMapper inviteMapper;
  @Autowired private UserMapper userMapper;
  @Autowired private IBalanceService balanceService;
  @Autowired private ServiceFuwuMapper serviceFuwuMapper;
  @Autowired private IUserService userService;

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public boolean inputInviterCode(UserDtlVO userDtlVO, String code, User inviter) {
    // 保存用户的邀请记录
    inviteMapper.insertSelective(new Invite(1, inviter.getUid(), code, userDtlVO.getUid()));
    // 更新邀请人信息
    boolean flag = userMapper.updateInviterCode(userDtlVO.getUid(), code, inviter.getUid()) == 1;
    // 在邀请人的通知里添加一条 join in 通知
    noticeService.addJoinPunchNotice(inviter.getUid(), userDtlVO.getNickName());
    // 输入邀请码 奖励2元
    if (userMapper.updateBalanceAdd(userDtlVO.getUid(), new BigDecimal("2")) == 1) {
      balanceService.addBalanceDtl(
          new BalanceDtl(userDtlVO.getUid(), "+2", "输入邀请码【" + code + "】奖励"),
          BalanceDtlTypeCodes._firstWithdraw);
    }
    taskExecutor.execute(() -> userService.changeUserCache(userDtlVO.getUid()));
    return flag;
  }

  @Override public boolean sendInvitedMessage(UserDtlVO currUserDtlVo, List<String> aliUids) {
    aliUids.forEach(aliUid -> {
      // todo nothings
    });
    return true;
  }

  @Override public List<Invite> getAllWithPage(int pageNum, int pageSize) {

    return this.getByUidWithPage(pageNum, pageSize, 0L);
  }

  @Override public List<Invite> getByUidWithPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(Invite.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    PageHelper.startPage(pageNum, pageSize);
    return inviteMapper.selectByExample(example);
  }

  @Override public ServiceFuwu getFuwuByType(FuwuTypeCodes fuwuTypeCodes) {
    String key = (RedisConstants.FUWU_KEY_PREFIX + fuwuTypeCodes.getCode()).intern();
    ServiceFuwu fuwu = (ServiceFuwu) redisUtils.get(key);
    if (null != fuwu) {
      log.debug("redis fuwu:{}", fuwu);
      return fuwu;
    }
    Example example = new Example(ServiceFuwu.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("type", fuwuTypeCodes.getCode())
        .andEqualTo("status", YesOrNoCodes.YES.getCode());
    example.orderBy("id").desc();
    fuwu = serviceFuwuMapper.selectOneByExample(example);
    redisUtils.set1Month(key, fuwu);
    return fuwu;
  }
}
