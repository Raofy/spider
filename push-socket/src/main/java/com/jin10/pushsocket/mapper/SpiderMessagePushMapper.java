package com.jin10.pushsocket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jin10.pushsocket.entity.SpiderMessagePush;

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
