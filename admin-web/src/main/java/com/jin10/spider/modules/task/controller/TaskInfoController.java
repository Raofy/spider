package com.jin10.spider.modules.task.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.service.UrlTaskProcessService;
import com.jin10.spider.modules.task.service.impl.UrlTaskMangerImpl;
import com.jin10.spider.modules.template.service.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 任务信息 前端控制器
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
@RestController
@RequestMapping("/template")
public class TaskInfoController {

    @Autowired
    private UrlTaskMangerImpl taskManger;
    @Autowired
    private UrlTaskProcessService taskProcessService;
    @Autowired
    private ITemplateService templateService;


    @PostMapping("/creatTask")
    public BaseResponse saveOrUpdate(){
        List<UrlTask> tasks = taskManger.creatTask();
        return BaseResponse.ok(tasks);
    }


    @PostMapping("/getTasks")
    public BaseResponse getTasks(){
        List<UrlTaskDto> tasks = taskProcessService.getTasks(false);
        return BaseResponse.ok(tasks);
    }
}
