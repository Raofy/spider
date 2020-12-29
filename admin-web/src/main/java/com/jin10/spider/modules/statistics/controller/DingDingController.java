package com.jin10.spider.modules.statistics.controller;

import cn.hutool.core.map.MapUtil;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.modules.statistics.bean.BaseWarnBean;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Airey
 * @date 2020/1/7 20:16
 * ----------------------------------------------
 * 爬虫钉钉警告
 * ----------------------------------------------
 */
@RestController
@RequestMapping("ding")
@Slf4j
public class DingDingController {

    @Autowired
    private IDingTalkWarnService warnService;

    private Map<String, BaseWarnBean> warnMap = new HashMap<>();


    /**
     * 没有足够的机器处理预警
     *
     * @param params
     * @return
     */
    @PostMapping("websocket")
    public BaseResponse webSocket(@RequestBody Map<String, Object> params) {

        String warnMsg = MapUtil.getStr(params, "warnMsg");
        String type = MapUtil.getStr(params, "type");
        warnService.secretKeyMsg(warnMsg);
        if (warnMap.containsKey(type)) {
            BaseWarnBean baseWarnBean = warnMap.get(type);
            if (baseWarnBean.whePush()) {
                baseWarnBean.addTimes();
                warnMsg += " ,警告次数: " + baseWarnBean.getTimes();
                warnService.msgWarn(warnMsg);
            }
            warnMap.put(type, baseWarnBean);
        } else {
            BaseWarnBean baseWarnBean = new BaseWarnBean(5, 30, 60, type);
            warnMap.put(type, baseWarnBean);
            warnService.msgWarn(warnMsg);
        }

        return BaseResponse.ok();
    }


}
