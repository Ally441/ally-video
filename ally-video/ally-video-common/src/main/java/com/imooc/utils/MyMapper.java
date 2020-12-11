package com.imooc.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author allycoding
 * @Date: 2020/6/12 16:11
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
