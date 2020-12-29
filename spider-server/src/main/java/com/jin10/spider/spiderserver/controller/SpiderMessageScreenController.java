package com.jin10.spider.spiderserver.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.jin10.spider.spiderserver.service.ISpiderMessageScreenService;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 筛选爬虫消息列表 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-12-09
 */
@RestController
@RequestMapping("/spider-message-screen")
@Slf4j
public class SpiderMessageScreenController {


    @Autowired
    private ISpiderMessageScreenService screenService;

    /**
     * 查询最新的分拣消息列表
     *
     * @return
     */
    @PostMapping("list")
    public BaseResponse list(@RequestBody Map<String, Object> params) {

        Long userId = MapUtil.getLong(params, "userId");
        if (ObjectUtil.isNull(userId)) {
            throw new BaseException("参数异常！！！userId不能为空！");
        }
        log.info("开始查询 userId = " + userId + " 的分拣消息列表！");

        LambdaQueryWrapper<SpiderMessageScreen> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SpiderMessageScreen::getInsertTime).eq(SpiderMessageScreen::getUserId, userId);
        List<SpiderMessageScreen> list = screenService.list(queryWrapper);
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
     * 清空分拣消息列表
     *
     * @return
     */
    @PostMapping("delete")
    public BaseResponse delete(@RequestBody Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        if (ObjectUtil.isNull(userId)) {
            throw new BaseException("参数异常！！！userId不能为空！");
        }
        screenService.deleteAll(userId);
        return BaseResponse.ok();

    }


}
