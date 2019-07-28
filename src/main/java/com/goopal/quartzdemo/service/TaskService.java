package com.goopal.quartzdemo.service;

import com.goopal.quartzdemo.model.CustomJobDetail;

import java.util.List;
import java.util.Map;

/**
 * @author xinj.x
 */
public interface TaskService {
  List<Map<String, Object>> getAllJobs();

  void addTask(CustomJobDetail job);

  void changeStatus(Long jobId, String cmd) throws Exception;

  void updateCron(Long jobId, String cron) throws Exception;
}
