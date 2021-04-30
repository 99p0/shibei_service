package cn.birdplanet.schedulerx.common.support.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 所有Mapper 的基类
 * <p>
 * Created by final.young@hotmail.com on 16/6/16.
 */
public interface MrMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
