/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.Notice;
import java.time.LocalDate;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IUserService
 * @date 2019-06-06 16:15
 */
public interface INoticeService {

  boolean addPunchResultNoticeForGM(long uid, GameMode gameMode, int round, boolean punchSucc,
      String checkinTime);
  boolean addPunchResultNotice(long uid, LocalDate punchDate, int round, boolean punchSucc,
      String checkinTime);

  boolean addCheckinResultNoticeForNormalMode(long uid, LocalDate punchDate, String title, boolean punchSucc,
      String checkinTime);
  boolean addPunchResultNoticeForNormalMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime);

  boolean addCheckinResultNoticeForRoomMode(long uid, LocalDate punchDate, String title, boolean punchSucc,
      String checkinTime);

  boolean addPunchResultNoticeForRoomMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime);

  boolean addJoinPunchNotice(long uid, String inviteName);

  List<Notice> getByUidWithPage(int pageNum, int pageSize, long uid);

  boolean noticeRead(long uid, long id);

  List<Notice> getAllWithPage(int pageNum, int pageSize);
}
