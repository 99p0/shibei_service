package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.CronTask;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IScheduledTaskService
 * @date 2019/11/8 16:11
 */
public interface IScheduledTaskService {

  List<CronTask> getAllScheduledTask();

  List<CronTask> getAllAvailableScheduledTask();
}
