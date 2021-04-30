package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.dto.NormalModeDTO;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.NormalModeRound;
import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.ActivityUserVo;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import com.github.pagehelper.PageInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: INormalModeActivityService
 * @date 2019/10/11 00:47
 */
public interface INormalModeService {

  /**
   * 获取可用的模版
   *
   * @return 模版集合
   */
  List<NormalModeTemplate> getAvailableTemplate();

  /**
   * 生成下个周期的活动
   */
  boolean generateNextPeriodActivityWithTemplate();

  List<NormalMode> getAllPunchingActivities();

  List<NormalMode> getAllEndOrPunchingAndNotSettledActivities();

  List<NormalMode> getAllEndAndNotSettledActivities();

  List<NormalMode> getAllActivitiesByPage(ActivityStatusCodes statusCodes, int pageNum,
      int pageSize);

  List<NormalMode> getAllActivitiesByPage(int pageNum, int pageSize);

  List<NormalModeTemplate> getAllTemplateByPage(int pageNum, int pageSize);

  List<NormalModeOrder> getOrdersWithPage(long uid, int status, int pageNum, int pageSize);

  List<NormalModeOrder> getOrdersForJoinedWithPage(long uid, int status, int pageNum,
      int pageSize);

  List<NormalModeOrder> getOrdersForPunching(long uid);

  List<NormalModeOrder> getOrdersForRegistered(long uid);

  List<NormalMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize);

  PageInfo<NormalModeDTO> getPlazaActivities();

  String getPlazaRKEY();

  NormalMode getActivityByIdFromRedis(long id);

  NormalMode getActivityById(long id);

  NormalModeOrder getOrderByActivityId(long aid, long uid);

  List<NormalModeOrder> getOrdersByActivityId(long aid);
  List<NormalModeOrder> getNoFailOrdersByActivityId(long aid);

  boolean join(NormalMode activity, User user, long id, String period,
      BigDecimal totalAmount, int multiple);

  int updateStatusForActivityExpired(LocalDateTime endTime);

  int updateStatusForActivityStart(LocalDateTime startTime);

  String getRKeyForActivity(long aid);

  String getRKeyForJoinedUsers(long aid);

  NormalModeDTO normalMode2Dto(NormalMode record);

  List<ActivityUserVo> getJoinedUsersByActivityId(long id);

  List<NormalModeRound> getRounds(long oid, Long uid);

  NormalModeOrder getOrderById(long oid, long uid);

  String checkin(long uid, NormalModeOrder order, LocalDateTime punchAt);

  boolean checkInPunchingTime(NormalModeOrder order);

  List<NormalMode> getTodayEndActivity(LocalDateTime endAt);

  long triggerCheckInFailureByActivity(NormalMode activity);

  long settlePrincipalForActivity(NormalMode activity);

  long settleBonusForActivity(NormalMode activity);
}
