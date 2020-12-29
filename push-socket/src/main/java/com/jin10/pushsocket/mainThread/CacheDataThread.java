package com.jin10.pushsocket.mainThread;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jin10.pushsocket.constants.DataCache;
import com.jin10.pushsocket.entity.SpiderCategory;
import com.jin10.pushsocket.entity.SpiderLabel;
import com.jin10.pushsocket.service.ISpiderCategoryService;
import com.jin10.pushsocket.service.ISpiderLabelService;
import lombok.extern.slf4j.Slf4j;
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


    }


}
