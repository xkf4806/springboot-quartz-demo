package com.goopal.quartzdemo.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

/**
 * @author xinj.x
 */
@Slf4j
public class HelloJobListener extends JobListenerSupport {
  private static final String LISTENER_NAME = "Hello_Listener";
  @Override
  public String getName() {
    return LISTENER_NAME;
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    log.info("任务即将执行，详情：【{}】", context.getJobDetail());
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {
    super.jobExecutionVetoed(context);
  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    log.info("任务执行完成，执行时间：{}，下次触发时间：{}", DateFormatUtils.format(context.getFireTime(), "yyyy-MM-dd HH:mm:ss"),DateFormatUtils.format(context.getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
  }
}
