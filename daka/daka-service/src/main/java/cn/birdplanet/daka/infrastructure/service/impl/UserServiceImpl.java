/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.BalanceDtl;
import cn.birdplanet.daka.domain.po.ServiceQQ;
import cn.birdplanet.daka.domain.po.ServiceWX;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.BaseInfoVO;
import cn.birdplanet.daka.infrastructure.persistence.punch.ServiceQQMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.ServiceWXMapper;
import cn.birdplanet.daka.infrastructure.persistence.punch.UserMapper;
import cn.birdplanet.daka.infrastructure.service.IBalanceService;
import cn.birdplanet.daka.infrastructure.service.IIdGenerateService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.BalanceDtlTypeCodes;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.UserTypeCodes;
import cn.birdplanet.toolkit.extra.code.YesOrNoCodes;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.github.pagehelper.PageHelper;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: UserServiceImpl
 * @date 2019-06-06 16:16
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseService implements IUserService {

  @Autowired private IIdGenerateService idGenerateService;
  @Autowired private UserMapper userMapper;
  @Autowired private IBalanceService balanceService;

  @Override public List<User> getAllByPage(int pageNum, int pageSize) {
    Example example = new Example(User.class);
    example.orderBy("createdAt").desc();
    PageHelper.startPage(pageNum, pageSize);
    return userMapper.selectByExample(example);
  }

  @Transactional(readOnly = true)
  @Override
  public synchronized User getByInvitationCode(String code) {
    return userMapper.getByInvitationCode(code);
  }

  @Transactional(readOnly = true)
  @Override
  public synchronized User getByUid(long userId) {
    Example example = new Example(User.class);
    example.createCriteria().andEqualTo("uid", userId);
    User user = userMapper.selectOneByExample(example);
    this.changeUserCache(user);
    return user;
  }

  @Transactional(readOnly = true)
  @Override
  public User getByUidFromRedis(long userId) {
    String key = RedisConstants.USER_DETAIL_KEY_PREFIX + userId;
    User user = (User) redisUtils.get(key);
    if (null == user) {
      user = this.changeUserCache(userId);
    }
    return user;
  }

  @Override
  public User changeUserCache(long userId) {
    User user = this.getByUid(userId);
    String key = RedisConstants.USER_DETAIL_KEY_PREFIX + user.getUid();
    redisUtils.set1Day(key, user);
    return user;
  }

  private void changeUserCache(User user) {
    if (null != user) {
      String key = RedisConstants.USER_DETAIL_KEY_PREFIX + user.getUid();
      redisUtils.set1Day(key, user);
    }
  }

  @Override public String generateInviteCode() {
    String inviteCode = RandomStringUtils.randomNumeric(6);
    User user = this.getByInvitationCode(inviteCode);
    if (null != user) {
      this.generateInviteCode();
    }
    return inviteCode;
  }

  @Override public User getByOpenUid(String openUid, UserTypeCodes userTypeCodes) {
    if (StringUtils.isBlank(openUid)) return null;
    String key = "user:" + userTypeCodes.getCode() + ":" + openUid;
    User user = (User) redisUtils.get(key);
    if (null == user) {
      Example example = new Example(User.class);
      Example.Criteria criteria = example.createCriteria();
      criteria.andEqualTo("openUid", openUid);
      criteria.andEqualTo("userType", userTypeCodes.getCode());
      user = userMapper.selectOneByExample(example);
      if (null != user) {
        redisUtils.set1Month(key, user);
      }
    } else {
      //
      key = RedisConstants.USER_DETAIL_KEY_PREFIX + user.getUid();
      User _user = (User) redisUtils.get(key);
      if (null != _user) {
        return _user;
      }
    }
    return user;
  }

  @Override public boolean uploadMoneyQr(long uid, String moneyQrUrl) {
    return userMapper.updateMoneyQrByUid(uid, moneyQrUrl) == 1;
  }

  @Override public boolean save(User user) {
    if (null == user) return false;
    if (null == user.getUid() || user.getUid() == 0) {
      user.setUid(idGenerateService.nextUid());
    }
    boolean flag = userMapper.insertSelective(user) == 1;
    if (flag) {
      // 保存用户的RSA密钥

    }
    return flag;
  }

  @Override public boolean updateNotAlertInviteView(long uid, String status) {
    return userMapper.updateNotAlertInviteView(uid, status) == 1;
  }

  @Override
  public boolean updateUserBaseInfo(long uid, String nickName, String avatar, String mobile,
      String isCertified, String gender) {
    return userMapper.updateUserBaseInfo(uid, nickName, avatar, mobile, isCertified, gender) == 1;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override
  public boolean rechargeSuccess(TradeChannelCodes tradeChannelCode, BigDecimal amount,
      long uid) {
    boolean flag = userMapper.updateBalanceAdd(uid, amount) == 1;
    if (flag) {
      BalanceDtl balanceDtl = new BalanceDtl(uid, "+" + amount.intValue(),
          TradeChannelCodes.alipay.getDesc() + "充值");
      balanceService.addBalanceDtl(balanceDtl, BalanceDtlTypeCodes._rechargeWithAlipay);
    } else {
      throw new RuntimeException("充值成功后，更新余额失败");
    }
    return flag;
  }

  @Autowired private ServiceQQMapper serviceQQMapper;
  @Autowired private ServiceWXMapper serviceWXMapper;

  @Override public BaseInfoVO getBaseInfo() {
    BaseInfoVO vo = (BaseInfoVO) redisUtils.get(RedisConstants.BASEINFO);
    // 重新获取
    if (null == vo) {
      vo = new BaseInfoVO(this.getBaseInfo_WX(), this.getBaseInfo_QQ());
      vo.setGreyscale(this.getBaseInfo_Grey());

      vo.setGameRule(this.getBaseInfo_GameRule());
      vo.setNormalRule(this.getBaseInfo_NormalRule());
      vo.setRoomRule(this.getBaseInfo_RoomRule());

      vo.setGameGrid(this.getBaseInfo_GameGrid());
      vo.setNormalGrid(this.getBaseInfo_NormalGrid());
      vo.setRoomGrid(this.getBaseInfo_RoomGrid());
      redisUtils.set1Month(RedisConstants.BASEINFO, vo);
    }
    return vo;
  }

  @Override public String getBaseInfo_GameRule() {
    String rule = (String) redisUtils.get(RedisConstants.BASEINFO_GAME_RULE);
    return StringUtils.isBlank(rule) ? "" : rule;
  }

  @Override public String getBaseInfo_GameGrid() {
    String grid = (String) redisUtils.get(RedisConstants.BASEINFO_GAME_GRID);
    return StringUtils.isBlank(grid) ? "" : grid;
  }

  @Override public String getBaseInfo_NormalRule() {
    String rule = (String) redisUtils.get(RedisConstants.BASEINFO_NORMAL_RULE);
    return StringUtils.isBlank(rule) ? "" : rule;
  }

  @Override public String getBaseInfo_NormalGrid() {
    String grid = (String) redisUtils.get(RedisConstants.BASEINFO_NORMAL_GRID);
    return StringUtils.isBlank(grid) ? "" : grid;
  }

  @Override public String getBaseInfo_RoomRule() {
    String rule = (String) redisUtils.get(RedisConstants.BASEINFO_ROOM_RULE);
    return StringUtils.isBlank(rule) ? "" : rule;
  }

  @Override public String getBaseInfo_RoomGrid() {
    String grid = (String) redisUtils.get(RedisConstants.BASEINFO_ROOM_GRID);
    return StringUtils.isBlank(grid) ? "" : grid;
  }

  @Transactional(rollbackFor = RuntimeException.class)
  @Override public synchronized boolean rechargeByUid(long uid, int amount) {
    //
    boolean flag = userMapper.updateBalanceAdd(uid, new BigDecimal(amount)) == 1;
    if (flag) {
      balanceService.addBalanceDtl(
          new BalanceDtl(uid, "+"+amount, BalanceDtlTypeCodes._rechargeWithConsole.getDesc()),
          BalanceDtlTypeCodes._rechargeWithConsole);
    }
    return flag;
  }

  private boolean getBaseInfo_Grey() {
    Boolean grey = (Boolean) redisUtils.get(RedisConstants.BASEINFO_grey);
    // 重新获取
    if (null == grey) {
      grey = false;
      redisUtils.set1Month(RedisConstants.BASEINFO_grey, grey);
    }
    return grey;
  }

  private ServiceQQ getBaseInfo_QQ() {
    ServiceQQ qq = (ServiceQQ) redisUtils.get(RedisConstants.BASEINFO_QQ);
    // 重新获取
    if (null == qq) {
      Example example = new Example(ServiceQQ.class);
      example.createCriteria().andEqualTo("status", YesOrNoCodes.YES.getCode());
      qq = serviceQQMapper.selectOneByExample(example);
      redisUtils.set1Month(RedisConstants.BASEINFO_QQ, qq);
    }
    return qq;
  }

  private ServiceWX getBaseInfo_WX() {
    ServiceWX wx = (ServiceWX) redisUtils.get(RedisConstants.BASEINFO_WX);
    // 重新获取
    if (null == wx) {
      Example example = new Example(ServiceWX.class);
      example.createCriteria().andEqualTo("status", YesOrNoCodes.YES.getCode());
      wx = serviceWXMapper.selectOneByExample(example);
      redisUtils.set1Month(RedisConstants.BASEINFO_WX, wx);
    }
    return wx;
  }
}
