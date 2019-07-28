package com.goopal.quartzdemo.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author xinj.x
 */
@Slf4j
public class HelloJob {

  public void execute() {
    log.info("欢迎您，这里是HelloJob，时间：{}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
  }
}
