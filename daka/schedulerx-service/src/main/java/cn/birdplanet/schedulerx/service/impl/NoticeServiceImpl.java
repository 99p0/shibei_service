/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service.impl;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.Notice;
import cn.birdplanet.schedulerx.persistence.punch.NoticeMapper;
import cn.birdplanet.schedulerx.service.INoticeService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.extra.code.NoticeTypeCodes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: NoticeServiceImpl
 * @date 2019-07-18 03:20
 */
@Slf4j
@Service
public class NoticeServiceImpl extends BaseService implements INoticeService {

  @Autowired private NoticeMapper noticeMapper;

  private String buildPunchNoticeContentForGM(GameMode gameMode, int round, boolean punchSucc) {
    return new StringBuilder(PunchUtils.punchPeriod(gameMode.getPeriod()))
        .append("期「")
        .append(gameMode.getTitle())
        .append("」第")
        .append(round)
        .append("轮闯关")
        .append(punchSucc ? "成功" : "失败")
        .toString();
  }

  private String buildPunchNoticeContent(LocalDate punchDate, int round, boolean punchSucc) {
    return new StringBuilder(PunchUtils.punchPeriod(punchDate)).append("期第")
        .append(round)
        .append("轮闯关")
        .append(punchSucc ? "成功" : "失败")
        .toString();
  }

  private String buildCheckinNoticeContent(LocalDate punchDate, String title, boolean punchSucc) {
    return new StringBuilder("【").append(title)
        .append("】签到")
        .append(punchSucc ? "成功" : "失败")
        .toString();
  }

  private String buildContentForNormalMode(LocalDate punchDate, String title, boolean punchSucc) {
    return new StringBuilder("【").append(title)
        .append("】打卡")
        .append(punchSucc ? "成功" : "失败")
        .toString();
  }

  private boolean add(Notice notice, NoticeTypeCodes noticeTypeCodes) {
    notice.setNoticeType(noticeTypeCodes.getCode());
    if (null == notice.getCreatedAt()) {
      notice.setCreatedAt(LocalDateTime.now());
    }
    int rows = noticeMapper.insertSelective(notice);
    return 1 == rows;
  }

  @Override
  public boolean addPunchResultNotice(long uid, GameMode gameMode, int round, boolean punchSucc,
      String checkinTime) {
    Notice notice = new Notice(uid, this.buildPunchNoticeContentForGM(gameMode, round, punchSucc));
    notice.setCheckinTime(checkinTime);
    return this.add(notice, punchSucc ? NoticeTypeCodes.punch_succ : NoticeTypeCodes.punch_err);
  }

  @Override
  public boolean addPunchResultNoticeForNormalMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime) {
    Notice notice = new Notice(uid, this.buildContentForNormalMode(punchDate, title, punchSucc));
    notice.setCheckinTime(checkinTime);
    return this.add(notice, punchSucc ? NoticeTypeCodes.punch_succ : NoticeTypeCodes.punch_err);
  }
  @Override
  public boolean addPunchResultNoticeForRoomMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime) {
    Notice notice = new Notice(uid, this.buildContentForNormalMode(punchDate, title, punchSucc));
    notice.setCheckinTime(checkinTime);
    return this.add(notice, punchSucc ? NoticeTypeCodes.punch_succ : NoticeTypeCodes.punch_err);
  }

  //@CachePut(value = "noticeDtl")
  @Override
  public boolean addJoinPunchNotice(long uid, String inviteName) {
    Notice notice = new Notice(uid, "好友“" + inviteName + "”加入小鸟星球");
    return this.add(notice, NoticeTypeCodes.invited_user_join);
  }
}
