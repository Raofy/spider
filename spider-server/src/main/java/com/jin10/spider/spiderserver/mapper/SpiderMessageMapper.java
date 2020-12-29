package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SpiderMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 爬虫消息列表 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-11-18
 */
public interface SpiderMessageMapper extends BaseMapper<SpiderMessage> {


    /**
     * 查询最新的消息列表
     *
     * @param table 表名
     * @param size
     * @return
     */
    List<SpiderMessage> findLatestMessageList(String table, int size);


    /**
     * 查询补全的消息列表
     *
     * @param table 表名
     * @param time
     * @return
     */
    List<SpiderMessage> findCompleteMessageList(@Param("table") String table, @Param("time") Long time,@Param("endTime") Long endTime);


}
