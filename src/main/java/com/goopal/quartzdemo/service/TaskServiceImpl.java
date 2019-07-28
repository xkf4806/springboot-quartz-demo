package com.goopal.quartzdemo.service;

import com.google.common.collect.Lists;
import com.goopal.quartzdemo.dao.CustomJobDetailMapper;
import com.goopal.quartzdemo.model.CustomJobDetail;
import com.goopal.quartzdemo.utils.MapUtil;
import com.goopal.quartzdemo.utils.QuartzJobFactory;
import com.goopal.quartzdemo.utils.QuartzJobFactoryDisallowConcurrentExecution;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author aisino-xxy
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskServiceImpl implements TaskService {

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @Autowired
  private CustomJobDetailMapper jobDetailMapper;

  /**
   * 查询所有的定时任务
   */
  @Override
  public List<Map<String, Object>> getAllJobs() {
    List<CustomJobDetail> scheduleJobs = jobDetailMapper.selectAll();
    List<Map<String, Object>> results = Lists.newArrayList();
    Optional.of(scheduleJobs).ifPresent(jobs -> jobs.forEach(job -> {
      try {
        Map<String, Object> result = MapUtil.objectToMap(job);
        results.add(result);
      } catch (Exception e) {
        log.error("转换异常！", e);
      }
    }));
    return results;
  }

  /**
   * 添加一个定时任务
   */
  @Override
  public void addTask(CustomJobDetail job) {
    job.setCreateTime(new Date());
    jobDetailMapper.insertSelective(job);
  }

  /**
   * 更改任务状态
   *
   * @throws SchedulerException
   */
  @Override
  public void changeStatus(Long jobId, String cmd) throws Exception {
    CustomJobDetail job = getTaskById(jobId);
    if(job == null) {
      return;
    }
    if("stop".equals(cmd)) {
      deleteJob(job);
      job.setJobStatus(CustomJobDetail.STATUS_NOT_RUNNING);
    } else if("start".equals(cmd)) {
      job.setJobStatus(CustomJobDetail.STATUS_RUNNING);
      addJob(job);
    }
    jobDetailMapper.updateByPrimaryKeySelective(job);
  }

  /**
   * 从数据库中查询job
   */
  public CustomJobDetail getTaskById(Long jobId) {
    return jobDetailMapper.selectByPrimaryKey(jobId);
  }

  /**
   * 更改任务 cron表达式
   *
   * @throws SchedulerException
   */
  @Override
  public void updateCron(Long jobId, String cron) throws Exception {
    CustomJobDetail job = getTaskById(jobId);
    if(job == null) {
      return;
    }
    job.setCronExpression(cron);
//    if(CustomJobDetail.STATUS_RUNNING.equals(job.getJobStatus())) {
      updateJobCron(job);
//    }
    job.setUpdateTime(new Date());
    jobDetailMapper.updateByPrimaryKeySelective(job);
  }

  /**
   * 添加任务
   *
   * @param job
   *
   * @throws SchedulerException
   */
  public void addJob(CustomJobDetail job) throws SchedulerException, ClassNotFoundException {
    if(job == null || CustomJobDetail.STATUS_RUNNING.equals(job.getJobStatus())) {
      return;
    }

    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    log.debug("get scheduler by schedulerFactoryBean...");

    TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

// 不存在，创建一个
    if(null == trigger) {
      Class clazz = CustomJobDetail.CONCURRENT_IS.equals(job.getIsConcurrent()) ? QuartzJobFactory.class :
              QuartzJobFactoryDisallowConcurrentExecution.class;
      JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
      jobDetail.getJobDataMap().put("scheduleJob", job);
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
      trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule
              (scheduleBuilder).build();
      scheduler.scheduleJob(jobDetail, trigger);
    } else {
// Trigger已存在，那么更新相应的定时设置
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
// 按新的cronExpression表达式重新构建trigger
      trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
// 按新的trigger重新设置job执行
      scheduler.rescheduleJob(triggerKey, trigger);
    }
  }

  /**
   * 删除一个job
   *
   * @param scheduleJob
   *
   * @throws SchedulerException
   */
  public void deleteJob(CustomJobDetail scheduleJob) throws SchedulerException {
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
    scheduler.deleteJob(jobKey);
  }

  /**
   * 立即执行job
   *
   * @param scheduleJob
   *
   * @throws SchedulerException
   */
  public void runAJobNow(CustomJobDetail scheduleJob) throws SchedulerException {
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
    scheduler.triggerJob(jobKey);
  }

  /**
   * 更新job时间表达式
   *
   * @param customJobDetail
   *
   * @throws SchedulerException
   */
  public void updateJobCron(CustomJobDetail customJobDetail) throws SchedulerException {
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    TriggerKey triggerKey = TriggerKey.triggerKey(customJobDetail.getJobName(), customJobDetail.getJobGroup());
    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(customJobDetail.getCronExpression());
    trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
    scheduler.rescheduleJob(triggerKey, trigger);
  }

  @PostConstruct
  public void init() {
    log.info("从数据库获取调度任务，准备触发执行......");
    List<CustomJobDetail> scheduleJobs = jobDetailMapper.selectAll();
    Optional.of(scheduleJobs).ifPresent(jobs -> jobs.forEach(job -> {
      try {
        addJob(job);
      } catch (Exception e) {
        log.error("================调度job异常================", e);
      }
    }));
  }
}