package com.jin10.spider.spiderserver.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessagePush;
import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.jin10.spider.spiderserver.service.ISpiderMessagePushService;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 推送爬虫消息列表 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2020-03-05
 */
@RestController
@RequestMapping("/spider-message-push")
@Slf4j
public class SpiderMessagePushController {


    @Autowired
    private ISpiderMessagePushService pushService;


    /**
     * 查询最新的推送消息列表
     *
     * @return
     */
    @GetMapping("list")
    public BaseResponse list() {
        log.info("开始查询推送的消息列表！");

        LambdaQueryWrapper<SpiderMessagePush> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(SpiderMessagePush::getInsertTime);
        queryWrapper.last("limit 200");

        List<SpiderMessagePush> list = pushService.list(queryWrapper);

        List<SpiderMessageVO> messageVOList = new ArrayList<>();
        list.forEach(
                item -> {
                    SpiderMessageVO messageVO = new SpiderMessageVO();
                    if (DataCache.categoryMap.containsKey(item.getCategory())) {
                        messageVO.setCategoryColor((String) DataCache.categoryMap.get(item.getCategory()));
                    }
                    BeanUtils.copyProperties(item, messageVO);
                    messageVOList.add(messageVO);
                }
        );

        JSONObject resultJson = new JSONObject();
        resultJson.put("list", messageVOList);
        resultJson.put("label", DataCache.labelMap);
        return BaseResponse.ok(resultJson);
    }

    /**
     * 清空推送消息列表
     *
     * @return
     */
    @GetMapping("delete")
    public BaseResponse delete() {
        log.info("开始清空推送的消息列表！");
        pushService.deleteAll();
        return BaseResponse.ok();

    }


}
