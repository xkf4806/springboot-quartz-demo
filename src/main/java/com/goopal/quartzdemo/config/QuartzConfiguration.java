package com.goopal.quartzdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author xinj.x
 */
@Configuration
public class QuartzConfiguration {

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//    schedulerFactoryBean.setAutoStartup(false);
    schedulerFactoryBean.setStartupDelay(10);
    return schedulerFactoryBean;
  }
}
