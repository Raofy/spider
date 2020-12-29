package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 筛选爬虫消息列表 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-12-09
 */
public interface SpiderMessageScreenMapper extends BaseMapper<SpiderMessageScreen> {

    /**
     * 清空分拣消息列表
     */
    void deleteAll(Long userId);

}
