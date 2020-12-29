package com.jin10.spider.modules.task.service.impl;

import com.jin10.spider.modules.task.entity.SpiderCountJob;
import com.jin10.spider.modules.task.mapper.SpiderCountJobMapper;
import com.jin10.spider.modules.task.service.ISpiderCountJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 每小时执行任务统计 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2020-03-10
 */
@Service
public class SpiderCountJobServiceImpl extends ServiceImpl<SpiderCountJobMapper, SpiderCountJob> implements ISpiderCountJobService {

    /**
     * 查询指定时间内的爬虫结果
     *
     * @param endTimeoff
     * @param endTime
     * @return
     */
    @Override
    public List<SpiderCountJob> findSpiderCountResult(long endTimeoff, long endTime) {
        return baseMapper.findSpiderCountResult(endTimeoff, endTime);
    }
}
