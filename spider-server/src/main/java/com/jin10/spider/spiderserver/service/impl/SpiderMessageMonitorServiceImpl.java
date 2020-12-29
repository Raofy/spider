package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.common.bean.BasePageRequest;
import com.jin10.spider.common.utils.QueryPage;
import com.jin10.spider.spiderserver.entity.SpiderMessageMonitor;
import com.jin10.spider.spiderserver.form.SpiderMessageMonitorForm;
import com.jin10.spider.spiderserver.mapper.SpiderMessageMonitorMapper;
import com.jin10.spider.spiderserver.service.ISpiderMessageMonitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.spiderserver.service.ITemplateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 消息来源监控 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-12-17
 */
@Service
public class SpiderMessageMonitorServiceImpl extends ServiceImpl<SpiderMessageMonitorMapper, SpiderMessageMonitor> implements ISpiderMessageMonitorService {


    @Autowired
    private ITemplateService templateService;


    /**
     * 更新或保存对应的记录
     *
     * @param monitor
     */
    @Override
    public void updateOrInsertRecord(SpiderMessageMonitor monitor) {

        LambdaQueryWrapper<SpiderMessageMonitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpiderMessageMonitor::getCategory, monitor.getCategory())
                .eq(SpiderMessageMonitor::getChannel, monitor.getChannel())
                .eq(SpiderMessageMonitor::getSource, monitor.getSource())
                .eq(SpiderMessageMonitor::getDispatch, monitor.getDispatch());
        try {
            SpiderMessageMonitor one = baseMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNull(one)) {
                if (ObjectUtil.isNotNull(monitor.getTempId())) {
                    String site = templateService.findSiteByTempId(monitor.getTempId().longValue());
                    monitor.setSite(site);
                }
                baseMapper.insert(monitor);
            } else {
                monitor.setId(one.getId());
                baseMapper.updateById(monitor);
            }
        } catch (Exception e) {
            log.error("数据库处理异常 ==》 " + e.getMessage());
        }


    }

    /**
     * 根据条件查询监控消息列表
     *
     * @param form
     * @return
     */
    @Override
    public IPage<SpiderMessageMonitor> queryForList(SpiderMessageMonitorForm form) {

        LambdaQueryWrapper<SpiderMessageMonitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SpiderMessageMonitor::getUpdateTime);

        if (StringUtils.isNotBlank(form.getCategory())) {
            queryWrapper.eq(SpiderMessageMonitor::getCategory, form.getCategory());
        }


        if (ObjectUtil.isNotNull(form.getAutoPush())) {
            queryWrapper.eq(SpiderMessageMonitor::getAutoPush, form.getAutoPush());
        }

        if (ObjectUtil.isNotNull(form.getDispatch())) {
            queryWrapper.eq(SpiderMessageMonitor::getDispatch, form.getDispatch());
        }

        if (StringUtils.isNotBlank(form.getKeyword())) {
            queryWrapper.nested(wq -> wq.like(SpiderMessageMonitor::getChannel, form.getKeyword()).or().like(SpiderMessageMonitor::getSource, form.getKeyword()));
        }


        return this.page(new QueryPage<SpiderMessageMonitor>().getPage(form), queryWrapper);
    }

    /**
     * 查询消息来源监控列表
     *
     * @return
     */
    @Override
    public IPage<SpiderMessageMonitor> findList(BasePageRequest pageRequest) {

        IPage<SpiderMessageMonitor> page = this.page(new QueryPage<SpiderMessageMonitor>().getPage(pageRequest), new LambdaQueryWrapper<SpiderMessageMonitor>().orderByDesc(SpiderMessageMonitor::getUpdateTime));
        return page;
    }

    /**
     * 根据分类查询
     *
     * @param form
     * @return
     */
    @Override
    public IPage<SpiderMessageMonitor> byCategory(SpiderMessageMonitorForm form) {

        IPage<SpiderMessageMonitor> page = this.page(new QueryPage<SpiderMessageMonitor>().getPage(form), new QueryWrapper<SpiderMessageMonitor>().eq("category", form.getCategory()).orderByDesc("update_time"));
        return page;
    }

    /**
     * 根据关键字查询channel或者source
     *
     * @param form
     * @return
     */
    @Override
    public IPage<SpiderMessageMonitor> search(SpiderMessageMonitorForm form) {
        LambdaQueryWrapper<SpiderMessageMonitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SpiderMessageMonitor::getChannel, form.getKeyword()).or().like(SpiderMessageMonitor::getSource, form.getKeyword()).orderByDesc(SpiderMessageMonitor::getUpdateTime);
        return this.page(new QueryPage<SpiderMessageMonitor>().getPage(form), queryWrapper);
    }


}
