package com.jin10.spider.modules.task.service;

import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.template.entity.Template;

import java.util.List;

/**
 * @author Airey
 * @date 2020/1/15 14:35
 * ----------------------------------------------
 * 生产任务逻辑处理类
 * ----------------------------------------------
 */
public interface IProductTaskService {


    /**
     * 初始化待运行的任务模板，加载到redis中
     */
    void initRunningTemplate();

    /**
     * 重新加载模板
     */
    void reloadRunningTemplate();


    /**
     * 产生待向队列中添加的任务列表
     *
     * @return
     */
    List<UrlTaskDto> createTask();

    /**
     * 开始生产调度任务
     */
    void startProductTask();

    /**
     * 根据template和urlTask组装实体类
     *
     * @param template
     * @param urlTask
     * @return
     */
    UrlTaskDto toUrlTaskDto(Template template, UrlTask urlTask);

}
