package com.goopal.quartzdemo.utils;

import com.goopal.quartzdemo.model.CustomJobDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xinj.x
 */
@Slf4j
public class TaskUtils {

  /**
   * 通过反射调用scheduleJob中定义的方法
   *
   * @param scheduleJob
   */
  public static void invokMethod(CustomJobDetail scheduleJob) {
    Object object = null;
    Class clazz;
    if(StringUtils.isNotBlank(scheduleJob.getSpringId())) {
      object = SpringUtils.getBean(scheduleJob.getSpringId());
    } else if(StringUtils.isNotBlank(scheduleJob.getBeanClass())) {
      try {
        clazz = Class.forName(scheduleJob.getBeanClass());
        object = clazz.newInstance();
      } catch (Exception e) {
// TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if(object == null) {
      log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，请检查是否配置正确！！！");
      return;
    }
    clazz = object.getClass();
    Method method = null;
    try {
      method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
    } catch (NoSuchMethodException e) {
      log.error("任务名称 = [{}]---------------未启动成功，方法名设置错误！！！", scheduleJob.getJobName());
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    if(method != null) {
      try {
        method.invoke(object);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

}
