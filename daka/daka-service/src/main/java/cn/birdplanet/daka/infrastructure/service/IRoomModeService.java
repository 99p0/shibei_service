package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.dto.RoomModeDTO;
import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.domain.po.RoomModeOrder;
import cn.birdplanet.daka.domain.po.RoomModeRound;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import com.github.pagehelper.PageInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IRoomModeService
 * @date 2019/10/11 00:47
 */
public interface IRoomModeService {

  /**
   * @return
   */
  List<RoomMode> getAllPunchingActivities();

  /**
   * @return
   */
  List<RoomMode> getAllEndOrPunchingAndNotSettledActivities();

  /**
   * @return
   */
  List<RoomMode> getAllEndAndNotSettledActivities();

  /**
   *
   * @param statusCodes
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<RoomMode> getAllActivitiesByPage(ActivityStatusCodes statusCodes, int pageNum,
      int pageSize);

  /**
   *
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<RoomMode> getAllActivitiesByPage(int pageNum, int pageSize);

  /**
   *
   * @param uid
   * @param status
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<RoomModeOrder> getOrdersWithPage(long uid, int status, int pageNum, int pageSize);

  /**
   *
   * @param uid
   * @param status
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<RoomModeOrder> getOrdersForJoinedWithPage(long uid, int status, int pageNum,
      int pageSize);

  /**
   *
   * @param uid
   * @return
   */
  List<RoomModeOrder> getOrdersForPunching(long uid);

  /**
   *
   * @param uid
   * @return
   */
  List<RoomModeOrder> getOrdersForRegistered(long uid);

  List<RoomMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize);

  PageInfo<RoomModeDTO> getPlazaActivities();

  RoomMode getActivityByIdFromRedis(long id);

  RoomMode getActivityById(long id);

  /**
   *
   * @param aid
   * @param uid
   * @return
   */
  RoomModeOrder getOrderByActivityId(long aid, long uid);

  /**
   *
   * @param aid
   * @return
   */
  List<RoomModeOrder> getOrdersByActivityId(long aid);

  /**
   *
   * @param aid
   * @return
   */
  List<RoomModeOrder> getNoFailOrdersByActivityId(long aid);

  /**
   *
   * @param activity
   * @param user
   * @param id
   * @param period
   * @param totalAmount
   * @param multiple
   * @return
   */
  boolean join(RoomMode activity, User user, long id, String period,
      BigDecimal totalAmount, int multiple);

  /**
   *
   * @param endTime
   * @return
   */
  int updateStatusForActivityExpired(LocalDateTime endTime);

  /**
   *
   * @param startTime
   * @return
   */
  int updateStatusForActivityStart(LocalDateTime startTime);

  /**
   *
   * @param aid
   * @return
   */
  String getRKeyForActivity(long aid);

  /**
   *
   * @param aid
   * @return
   */
  String getRKeyForJoinedUsers(long aid);

  /**
   *
   * @param record
   * @return
   */
  RoomModeDTO roomMode2Dto(RoomMode record);

  /**
   *
   * @param id
   * @return
   */
  List<ActivityUserVo> getJoinedUsersByActivityId(long id);

  /**
   *
   * @param oid
   * @param uid
   * @return
   */
  List<RoomModeRound> getRounds(long oid, Long uid);

  /**
   *
   * @param oid
   * @param uid
   * @return
   */
  RoomModeOrder getOrderById(long oid, long uid);

  /**
   *
   * @param uid
   * @param order
   * @param punchAt
   * @return
   */
  String checkin(long uid, RoomModeOrder order, LocalDateTime punchAt);

  /**
   *
   * @param order
   * @return
   */
  boolean checkInPunchingTime(RoomModeOrder order);
}
