package com.jin10.spider.modules.task.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.modules.task.entity.SpiderCountJob;
import com.jin10.spider.modules.task.service.ISpiderCountJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 每小时执行任务统计 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2020-03-09
 */
@RestController
@RequestMapping("/spider-count-job")
@Slf4j
public class SpiderCountJobController {

    @Autowired
    private ISpiderCountJobService spiderCountJobService;


    @GetMapping("getDetail/{endTime}")
    public BaseResponse getDetail(@PathVariable("endTime") Long endTime) {

        log.info("endTime => {} ", endTime);
        //ms转化为s
        endTime = endTime / 1000;
        List<SpiderCountJob> list = spiderCountJobService.findSpiderCountResult(endTime - 60, endTime + 60);

        return BaseResponse.ok(list);

    }






}
