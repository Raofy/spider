package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.Template;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 爬虫模版 服务类
 * </p>
 *
 * @author Airey
 * @since 2020-06-03
 */
public interface ITemplateService extends IService<Template> {


    /**
     * 根据id查询网址
     * @param tempId
     * @return
     */
    String findSiteByTempId(Long tempId);
    
}
