package com.jin10.spider.modules.task.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-08 16:27
 * ----------------------------------------------
 * <p>
 * 爬虫获取任务列表
 */
@Component
@ActionCode(value = ActionCodeConstants.GET_TASK)
public class GetTaskService implements IActionSocketService {

    @Autowired
    private UrlTaskProcessService taskProcessService;

    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        boolean foreign = false;
        List<UrlTaskDto> tasks;
        JSONObject json = JSONObject.parseObject(message);
        if (ObjectUtil.isNotNull(json.getBoolean("weforeign"))) {
            foreign = json.getBoolean("weforeign");
        }
        if (foreign) {
            tasks = taskProcessService.getTasks(true);
        } else {
            tasks = taskProcessService.getTasks(false);
        }
        return tasks;
    }
}
