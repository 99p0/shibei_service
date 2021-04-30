package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.dto.NormalModeDTO;
import cn.birdplanet.daka.domain.po.NormalMode;
import cn.birdplanet.daka.domain.po.NormalModeOrder;
import cn.birdplanet.daka.domain.po.NormalModeTemplate;
import cn.birdplanet.toolkit.extra.code.ActivityStatusCodes;
import com.github.pagehelper.PageInfo;
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

  List<NormalMode> getActivitiesForPlazaWithPage(int pageNum, int pageSize);

  PageInfo<NormalModeDTO> getPlazaActivities();

  String getPlazaRKEY();

  NormalMode getActivityByIdFromRedis(long id);

  NormalMode getActivityById(long id);

  List<NormalModeOrder> getOrdersByActivityId(long aid);

  List<NormalModeOrder> getNoFailOrdersByActivityId(long aid);

  int updateStatusForActivityExpired(LocalDateTime endTime);

  int updateStatusForActivityStart(LocalDateTime startTime);

  String getRKeyForActivity(long aid);

  NormalModeDTO normalMode2Dto(NormalMode record);

  NormalModeOrder getOrderById(long oid, long uid);

  boolean checkInPunchingTime(NormalModeOrder order);

  long triggerCheckInFailureByActivity(NormalMode activity);

  long settlePrincipalForActivity(NormalMode activity);

  long settleBonusForActivity(NormalMode activity);
}
