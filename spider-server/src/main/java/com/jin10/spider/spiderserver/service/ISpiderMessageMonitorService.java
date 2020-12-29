package com.jin10.spider.spiderserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.common.bean.BasePageRequest;
import com.jin10.spider.spiderserver.entity.SpiderMessageMonitor;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.spiderserver.form.SpiderMessageMonitorForm;


/**
 * <p>
 * 消息来源监控 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-12-17
 */
public interface ISpiderMessageMonitorService extends IService<SpiderMessageMonitor> {


    /**
     * 更新或保存对应的记录
     *
     * @param monitor
     */
    void updateOrInsertRecord(SpiderMessageMonitor monitor);


    /**
     * 根据条件查询列表
     *
     * @param form
     * @return
     */
    IPage<SpiderMessageMonitor> queryForList(SpiderMessageMonitorForm form);


    /**
     * 查询消息来源监控列表
     *
     * @return
     */
    IPage<SpiderMessageMonitor> findList(BasePageRequest pageRequest);


    /**
     * 根据分类查询
     *
     * @param form
     * @return
     */
    IPage<SpiderMessageMonitor> byCategory(SpiderMessageMonitorForm form);


    /**
     * 根据关键字查询channel或者source
     *
     * @param form
     * @return
     */
    IPage<SpiderMessageMonitor> search(SpiderMessageMonitorForm form);

}
