package com.goopal.quartzdemo.controller;

import com.alibaba.fastjson.JSON;
import com.goopal.quartzdemo.controller.param.RetObj;
import com.goopal.quartzdemo.model.CustomJobDetail;
import com.goopal.quartzdemo.service.TaskService;
import com.goopal.quartzdemo.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author xinj.x
 */
@Slf4j
@Controller
@RequestMapping(value = "/task")
public class TaskController {
  @Autowired
  private TaskService taskService;

  /**
   * 查询所有的定时任务
   *
   * @return
   */
  @RequestMapping("/taskList")
  public String taskList(Model model) {
    log.info("进入定时任务监控页面");

    List<Map<String, Object>> taskList = taskService.getAllJobs();
    model.addAttribute("taskList", taskList);
    return "taskList";
  }

  /**
   * 添加一个定时任务
   *
   * @param scheduleJob
   *
   * @return retObj
   */
  @RequestMapping("/add")
  @ResponseBody
  public String addTask(CustomJobDetail scheduleJob) {
    RetObj retObj = new RetObj();
    retObj.setFlag(false);
    try {
      CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
    } catch (Exception e) {
      retObj.setMsg("cron表达式有误，不能被解析！");
      return JSON.toJSONString(retObj);
    }

    Object obj = null;
    try {
      if(StringUtils.isNotBlank(scheduleJob.getSpringId())) {
        obj = SpringUtils.getBean(scheduleJob.getSpringId());
      } else {
        Class clazz = Class.forName(scheduleJob.getBeanClass());
        obj = clazz.newInstance();
      }
    } catch (Exception e) {
// do nothing.........
    }
    if(obj == null) {
      retObj.setMsg("未找到目标类！");
      return JSON.toJSONString(retObj);
    } else {
      Class clazz = obj.getClass();
      Method method = null;
      try {
        method = clazz.getMethod(scheduleJob.getMethodName(), null);
      } catch (Exception e) {
// do nothing.....
      }
      if(method == null) {
        retObj.setMsg("未找到目标方法！");
        return JSON.toJSONString(retObj);
      }
    }

    try {
      taskService.addTask(scheduleJob);
    } catch (Exception e) {
      e.printStackTrace();
      retObj.setFlag(false);
      retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
      return JSON.toJSONString(retObj);
    }

    retObj.setFlag(true);
    return JSON.toJSONString(retObj);
  }

  /**
   * 开启/关闭一个定时任务
   *
   * @param jobId
   * @param cmd
   *
   * @return
   */
  @RequestMapping("/changeJobStatus")
  @ResponseBody
  public String changeJobStatus(Long jobId, String cmd) {
    RetObj retObj = new RetObj();
    retObj.setFlag(false);
    try {
      taskService.changeStatus(jobId, cmd);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      retObj.setMsg("任务状态改变失败！");
      return JSON.toJSONString(retObj);
    }
    retObj.setFlag(true);
    return JSON.toJSONString(retObj);
  }

  /**
   * 修改定时任务的执行时间间隔
   *
   * @param jobId
   * @param cron
   *
   * @return
   */
  @RequestMapping("/updateCron")
  @ResponseBody
  public String updateCron(Long jobId, String cron) {
    RetObj retObj = new RetObj();
    retObj.setFlag(false);
    try {
      // 此处调用是为了校验cron表达式的正确性
      CronScheduleBuilder.cronSchedule(cron);
    } catch (Exception e) {
      retObj.setMsg("cron表达式有误，不能被解析！");
      return JSON.toJSONString(retObj);
    }
    try {
      taskService.updateCron(jobId, cron);
    } catch (Exception e) {
      retObj.setMsg("cron更新失败！");
      return JSON.toJSONString(retObj);
    }
    retObj.setFlag(true);
    return JSON.toJSONString(retObj);
  }

}
