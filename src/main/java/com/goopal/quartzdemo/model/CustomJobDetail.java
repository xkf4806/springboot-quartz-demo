package com.goopal.quartzdemo.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author xinj.x
 */
@Data
@ToString
@Table(name = "custom_job_detail")
public class CustomJobDetail {
  public static final String STATUS_RUNNING = "1";
  public static final String STATUS_NOT_RUNNING = "0";
  public static final String CONCURRENT_IS = "1";
  public static final String CONCURRENT_NOT = "0";
  @Id
  private Long jobId;

  private Date createTime;

  private Date updateTime;
  /**
   * 任务名称
   */
  private String jobName;
  /**
   * 任务分组
   */
  private String jobGroup;
  /**
   * 任务状态是否启动任务
   */
  private String jobStatus;
  /**
   * cron表达式
   */
  private String cronExpression;
  /**
   * 描述
   */
  private String description;
  /**
   * 任务执行时调用哪个类的方法包名+类名
   */
  private String beanClass;
  /**
   * 任务是否有状态
   */
  private String isConcurrent;
  /**
   * springbean
   */
  private String springId;
  /**
   * 任务调用的方法名
   */
  private String methodName;

}
