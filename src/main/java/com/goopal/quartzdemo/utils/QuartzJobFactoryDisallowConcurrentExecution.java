package com.goopal.quartzdemo.utils;

import com.goopal.quartzdemo.model.CustomJobDetail;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author xinj.x
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {
  @Override
  public void execute(JobExecutionContext context) {
    CustomJobDetail scheduleJob = (CustomJobDetail) context.getMergedJobDataMap().get("scheduleJob");
    TaskUtils.invokMethod(scheduleJob);
  }
}
