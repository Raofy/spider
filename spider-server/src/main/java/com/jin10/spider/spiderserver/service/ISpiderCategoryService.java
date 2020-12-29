package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SpiderCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 消息类别管理 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-29
 */
public interface ISpiderCategoryService extends IService<SpiderCategory> {


    /**
     * 获取前端消息列表
     *
     * @return
     */
    List<SpiderCategory> frontList();

    /**
     * 根据category查询是否存在
     *
     * @param category
     * @return
     */
    SpiderCategory selectOne(String category);

}
