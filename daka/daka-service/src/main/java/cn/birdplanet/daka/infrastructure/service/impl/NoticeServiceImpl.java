/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.GameMode;
import cn.birdplanet.daka.domain.po.Notice;
import cn.birdplanet.daka.infrastructure.persistence.punch.NoticeMapper;
import cn.birdplanet.daka.infrastructure.service.INoticeService;
import cn.birdplanet.toolkit.PunchUtils;
import cn.birdplanet.toolkit.extra.code.NoticeTypeCodes;
import com.github.pagehelper.PageHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
  public boolean addPunchResultNoticeForGM(long uid, GameMode gameMode, int round,
      boolean punchSucc, String checkinTime) {
    Notice notice = new Notice(uid, this.buildPunchNoticeContentForGM(gameMode, round, punchSucc));
    notice.setCheckinTime(checkinTime);
    return this.add(notice, punchSucc ? NoticeTypeCodes.punch_succ : NoticeTypeCodes.punch_err);
  }

  @Override
  public boolean addPunchResultNotice(long uid, LocalDate punchDate, int round, boolean punchSucc,
      String checkinTime) {
    Notice notice = new Notice(uid, this.buildPunchNoticeContent(punchDate, round, punchSucc));
    notice.setCheckinTime(checkinTime);
    return this.add(notice, punchSucc ? NoticeTypeCodes.punch_succ : NoticeTypeCodes.punch_err);
  }

  @Override
  public boolean addCheckinResultNoticeForNormalMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime) {
    Notice notice = new Notice(uid, this.buildCheckinNoticeContent(punchDate, title, punchSucc));
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
  public boolean addCheckinResultNoticeForRoomMode(long uid, LocalDate punchDate, String title,
      boolean punchSucc, String checkinTime) {
    Notice notice = new Notice(uid, this.buildCheckinNoticeContent(punchDate, title, punchSucc));
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

  //@Cacheable(value = "noticeDtl", key = "#uid + '_' + #pageNum+ '_' + #pageSize")
  @Override
  public List<Notice> getByUidWithPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(Notice.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    example.orderBy("createdAt").desc().orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return noticeMapper.selectByExample(example);
  }

  @Override
  public List<Notice> getAllWithPage(int pageNum, int pageSize) {
    return this.getByUidWithPage(pageNum, pageSize, 0L);
  }

  @Override public boolean noticeRead(long uid, long id) {
    return noticeMapper.updateNoticeRead(uid, id) == 1;
  }
}
