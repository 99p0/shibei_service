package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.CronTask;
import cn.birdplanet.daka.infrastructure.persistence.punch.CronTaskMapper;
import cn.birdplanet.daka.infrastructure.service.IScheduledTaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: ScheduledTaskServiceImpl
 * @date 2019/11/8 16:11
 */
@Service
public class ScheduledTaskServiceImpl extends BaseService implements IScheduledTaskService {

  @Autowired CronTaskMapper cronTaskMapper;

  @Override public List<CronTask> getAllScheduledTask() {
    return cronTaskMapper.selectAll();
  }

  @Override public List<CronTask> getAllAvailableScheduledTask() {
    Example example = new Example(CronTask.class);
    example.createCriteria().andEqualTo("status", "Y");
    return cronTaskMapper.selectByExample(example);
  }
}
