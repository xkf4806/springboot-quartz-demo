package com.goopal.quartzdemo.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author xinj.x
 */
@Configuration
@MapperScan("com.goopal.quartzdemo.dao")
public class MybatisConfig {

}
