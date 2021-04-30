/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import cn.birdplanet.schedulerx.persistence.punch.UserMapper;
import cn.birdplanet.schedulerx.service.IUserService;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

  @Autowired private UserMapper userMapper;

  @Transactional(readOnly = true)
  @Override
  public synchronized User getByUid(long userId) {
    Example example = new Example(User.class);
    example.createCriteria().andEqualTo("uid", userId);
    User user = userMapper.selectOneByExample(example);
    this.changeUserCache(user);
    return user;
  }

  @Override public List<PunchSumVO> getNotJoinedByMonth(LocalDate firstDay, LocalDate lastDay) {

    return userMapper.getNotJoinedByMonth(firstDay,lastDay);
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
}
