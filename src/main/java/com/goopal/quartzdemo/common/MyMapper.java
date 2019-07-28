package com.goopal.quartzdemo.common;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author xinj.x
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
