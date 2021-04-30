package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.RoomMode;
import cn.birdplanet.daka.domain.po.RoomModeOrder;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
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


  RoomMode getActivityByIdFromRedis(long id);

  RoomMode getActivityById(long id);

  /**
   * @param aid
   * @return
   */
  List<RoomModeOrder> getOrdersByActivityId(long aid);

  /**
   * @param aid
   * @return
   */
  List<RoomModeOrder> getNoFailOrdersByActivityId(long aid);

  /**
   * @param endTime
   * @return
   */
  int updateStatusForActivityExpired(LocalDateTime endTime);

  /**
   * @param startTime
   * @return
   */
  int updateStatusForActivityStart(LocalDateTime startTime);

  /**
   * @param aid
   * @return
   */
  String getRKeyForActivity(long aid);

  /**
   * @param aid
   * @return
   */
  String getRKeyForJoinedUsers(long aid);

  /**
   * @param id
   * @return
   */
  List<ActivityUserVo> getJoinedUsersByActivityId(long id);

  /**
   * @param oid
   * @param uid
   * @return
   */
  RoomModeOrder getOrderById(long oid, long uid);

  /**
   * @param order
   * @return
   */
  boolean checkInPunchingTime(RoomModeOrder order);

  /**
   * @param activity
   * @return
   */
  long triggerCheckInFailureByActivity(RoomMode activity);


  long settleRoomActivity(RoomMode activity);
}
