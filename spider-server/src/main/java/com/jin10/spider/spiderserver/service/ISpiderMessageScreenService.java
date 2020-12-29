package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 筛选爬虫消息列表 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-12-09
 */
public interface ISpiderMessageScreenService extends IService<SpiderMessageScreen> {


     void deleteAll(Long userId);


}
