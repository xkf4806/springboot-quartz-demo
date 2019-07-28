package com.goopal.quartzdemo.utils;

import com.goopal.quartzdemo.model.CustomJobDetail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author xinj.x
 */
public class QuartzJobFactory implements Job {
  @Override
  public void execute(JobExecutionContext context) {
    CustomJobDetail scheduleJob = (CustomJobDetail) context.getMergedJobDataMap().get("scheduleJob");
    TaskUtils.invokMethod(scheduleJob);
  }
}
