/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.GameMode;
import java.time.LocalDate;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IUserService
 * @date 2019-06-06 16:15
 */
public interface INoticeService {

  boolean addPunchResultNotice(long uid, GameMode gameMode, int round, boolean punchSucc,
      String checkinTime);

  boolean addPunchResultNoticeForNormalMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime);

  boolean addPunchResultNoticeForRoomMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime);

  boolean addJoinPunchNotice(long uid, String inviteName);

}
