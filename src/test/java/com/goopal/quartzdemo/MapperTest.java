package com.goopal.quartzdemo;

import com.goopal.quartzdemo.dao.CustomJobDetailMapper;
import com.goopal.quartzdemo.model.CustomJobDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xinj.x
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTest {
  @Autowired
  private CustomJobDetailMapper scheduleMapper;

  @Test
  public void testGetByPrimaryKey() {
    CustomJobDetail scheduleJob = scheduleMapper.selectByPrimaryKey(1L);
    log.info("schedule: {}", scheduleJob);
  }
}
