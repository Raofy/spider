package com.jin10.spider.spiderserver.mainThread;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderCategory;
import com.jin10.spider.spiderserver.entity.SpiderLabel;
import com.jin10.spider.spiderserver.entity.SpiderMessageMonitor;
import com.jin10.spider.spiderserver.service.ISpiderCategoryService;
import com.jin10.spider.spiderserver.service.ISpiderLabelService;
import com.jin10.spider.spiderserver.service.ISpiderMessageMonitorService;
import com.jin10.spider.spiderserver.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Airey
 * @date 2019/11/27 15:57
 * ----------------------------------------------
 * 初始化缓存map的值
 * ConcurrentHashMap不支持null键和null值
 * ----------------------------------------------
 */
@Component
@Slf4j
public class CacheDataThread implements CommandLineRunner {

    @Autowired
    private ISpiderLabelService labelService;
    @Autowired
    private ISpiderCategoryService categoryService;
    @Autowired
    private ISpiderMessageMonitorService monitorService;

    @Override
    public void run(String... args) throws Exception {
        log.warn("初始化缓存数据的值......");
        List<SpiderLabel> labelList = labelService.list(null);
        labelList.forEach(item -> {
            if (StringUtils.isNotBlank(item.getLabelName()) && StringUtils.isNotBlank(item.getLabelColor())) {
                DataCache.labelMap.put(item.getLabelName(), item.getLabelColor());
            }
        });

        log.warn("初始化labelMap成功！！！ lableMap的值为===》" + DataCache.labelMap);
        List<SpiderCategory> categoryList = categoryService.list(null);
        categoryList.forEach(e -> {
            if (StringUtils.isNotBlank(e.getCategoryName()) && StringUtils.isNotBlank(e.getCategoryColor())) {
                DataCache.categoryMap.put(e.getCategoryName(), e.getCategoryColor());
            }
        });
        log.warn("初始化categoryMap成功！！！categoryMap的值为 ===> " + DataCache.categoryMap);

        LambdaQueryWrapper<SpiderMessageMonitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpiderMessageMonitor::getAutoPush, 0);
        List<SpiderMessageMonitor> monitorList = monitorService.list(queryWrapper);
        monitorList.forEach(item -> {
            String md5Code = MD5Util.getMessageMonitorCode(item.getSource(), item.getCategory(), item.getChannel());
            String formatKey = StrUtil.format("{}:{}:{}", item.getSource(), item.getCategory(), item.getChannel());
            DataCache.autoPushMap.put(md5Code, formatKey);
        });
        log.warn("初始化autoPushMap成功！！！autoPushMap ===> " + DataCache.autoPushMap);
        return;
    }


}
