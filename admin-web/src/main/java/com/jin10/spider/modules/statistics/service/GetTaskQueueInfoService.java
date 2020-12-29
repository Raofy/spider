package com.jin10.spider.modules.statistics.service;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.modules.task.service.UrlTaskProcessService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-12-11 10:50
 * ----------------------------------------------
 */
@Component
@ActionCode(value = ActionCodeConstants.TASK_QUEUE_INFO)
public class GetTaskQueueInfoService implements IActionSocketService {
//
//    @Autowired
//    private ITemplateService templateService;

    @Autowired
    private UrlTaskProcessService urlTaskProcessService;

    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        ChannelSupervise.addChannelByActionCode(context.channel(),  ActionCodeConstants.TASK_QUEUE_INFO);
        return new MsgResponse(MsgCodeEnum.TASK_QUEUE_PRODUCT, urlTaskProcessService.getQueueDetailToPush());
    }
}
