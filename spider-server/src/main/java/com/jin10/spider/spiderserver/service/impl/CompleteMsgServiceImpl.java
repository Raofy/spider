package com.jin10.spider.spiderserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.spiderserver.entity.SpiderMessage;
import com.jin10.spider.spiderserver.service.ISpiderMessageService;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Airey
 * @date 2019/11/22 14:06
 * ----------------------------------------------
 * 处理客户端补全消息请求
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.COMPLETE_MSG)
@Slf4j
public class CompleteMsgServiceImpl implements IActionSocketService {

    @Autowired
    private ISpiderMessageService messageService;


    /**
     * 处理客户端补全消息请求
     * <p>
     * { "action": 301, "ip": "127.0.0.1", "data": { "time": 1574389771000 } }
     *
     * @param context
     * @param message
     * @return
     */
    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        log.info("开始处理业务请求消息: " + message);
        JSONObject resultJson = new JSONObject();
        try {
            JSONObject msgJson = JSONObject.parseObject(message);
            JSONObject data = (JSONObject) msgJson.get("data");
            Long time = (Long) data.get("time");
            List<SpiderMessage> completeMessageList = messageService.findCompleteMessageList(time);
            List<SpiderMessageVO> messageVOList = new ArrayList<>();
            completeMessageList.forEach(x -> {
                if (DataCache.categoryMap.containsKey(x.getCategory())) {
                    x.setCategoryColor((String) DataCache.categoryMap.get(x.getCategory()));
                }
                SpiderMessageVO messageVO=new SpiderMessageVO();
                BeanUtils.copyProperties(x,messageVO);
                messageVOList.add(messageVO);
            });
            resultJson.put("list", messageVOList);
            resultJson.put("label", DataCache.labelMap);
        } catch (Exception e) {
            log.error("message : " + message + " 处理异常！", e);
            return BaseResponse.error(e.getMessage());
        }
        return new MsgResponse(MsgCodeEnum.COMPLETE, resultJson);
    }
}
