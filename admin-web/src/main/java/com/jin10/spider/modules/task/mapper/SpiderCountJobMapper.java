package com.jin10.spider.modules.task.mapper;

import com.jin10.spider.modules.task.entity.SpiderCountJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 每小时执行任务统计 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2020-03-10
 */
@Mapper
public interface SpiderCountJobMapper extends BaseMapper<SpiderCountJob> {


    /**
     * 查询指定时间内的爬虫结果
     *
     * @param endTimeoff
     * @param endTime
     * @return
     */
    List<SpiderCountJob> findSpiderCountResult(@Param("endTimeOff") long endTimeoff, @Param("endTime") long endTime);

}
