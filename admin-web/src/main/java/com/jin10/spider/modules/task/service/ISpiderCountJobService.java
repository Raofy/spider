package com.jin10.spider.modules.task.service;

import com.jin10.spider.modules.task.entity.SpiderCountJob;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 每小时执行任务统计 服务类
 * </p>
 *
 * @author Airey
 * @since 2020-03-10
 */
public interface ISpiderCountJobService extends IService<SpiderCountJob> {


    /**
     * 查询指定时间内的爬虫结果
     *
     * @param endTimeoff
     * @param endTime
     * @return
     */
    List<SpiderCountJob> findSpiderCountResult(long endTimeoff, long endTime);


}
