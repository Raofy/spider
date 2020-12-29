package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.service.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 爬虫模版 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2020-06-03
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private ITemplateService templateService;


    @GetMapping("findById")
    public BaseResponse findById(){
        String site = templateService.findSiteByTempId(3452321L);
        return BaseResponse.ok(site);
    }

}
