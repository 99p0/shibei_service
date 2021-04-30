/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PunchSumVO;
import java.time.LocalDate;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IUserService
 * @date 2019-06-06 16:15
 */
public interface IUserService {

  User getByUidFromRedis(long uid);

  User changeUserCache(long userId);

  User getByUid(long uid);

  List<PunchSumVO> getNotJoinedByMonth(LocalDate firstDay, LocalDate lastDay);
}
