package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SpiderMessagePush;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 推送爬虫消息列表 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2020-03-05
 */
public interface SpiderMessagePushMapper extends BaseMapper<SpiderMessagePush> {


    /**
     * 清空推送列表
     */
    void deleteAll();

}
