package com.jin10.pushsocket.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.pushsocket.annotation.ActionCode;
import com.jin10.pushsocket.constants.ActionCodeConstants;
import com.jin10.pushsocket.entity.SpiderMessageElastics;
import com.jin10.pushsocket.entity.TerminalRecordElastics;
import com.jin10.pushsocket.interf.IActionSocketService;
import com.jin10.pushsocket.service.ISpiderMsgElasticSearchService;
import com.jin10.pushsocket.service.ITerminalRecordElasticsService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author Airey
 * @date 2019/12/11 17:15
 * ----------------------------------------------
 * 处理终端消费消息记录
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TERMINAL_RECORD)
@Slf4j
public class TerminalRecordServiceImpl implements IActionSocketService {


    @Autowired
    private ITerminalRecordElasticsService recordElasticsService;

    @Autowired
    private ISpiderMsgElasticSearchService msgElasticSearchService;


    /**
     * 处理终端消费消息记录
     * 存入ES中
     * <p>
     * {
     * "action": 380,
     * "ip": "127.0.0.1",
     * "data": {
     * "userName": "admin",
     * "machineCode": "34efeqr4353f3425243ty567465hg5637y36"
     * "msgId": "884babf2c927471684be1fa44f94afcf",
     * "onlineTime": 1576045431000,
     * "offlineTime": 1576045431000,
     * <p>
     * }
     * }
     *
     * @param context
     * @param message
     * @return
     */
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);

        try {
            JSONObject msgJson = JSONObject.parseObject(message);

            JSONObject data = msgJson.getJSONObject("data");

            String userName = data.getString("userName");
            String msgId = data.getString("msgId");
            String machineCode = data.getString("machineCode");
            Long onlineTime = data.getLong("onlineTime");
            Long offlineTime = data.getLong("offlineTime");

            TerminalRecordElastics recordElastics = new TerminalRecordElastics();

            recordElastics.setUserName(userName);
            recordElastics.setMsgId(msgId);
            recordElastics.setMachineCode(machineCode);
            recordElastics.setOnlineTime(DateUtil.date(onlineTime));
            recordElastics.setOfflineTime(DateUtil.date(offlineTime));

            Optional<SpiderMessageElastics> spMsgOptional = msgElasticSearchService.findById(msgId);
            if (spMsgOptional.isPresent()) {
                SpiderMessageElastics spiderMessageElastics = spMsgOptional.get();
                BeanUtils.copyProperties(spiderMessageElastics, recordElastics);
                recordElasticsService.save(recordElastics);
                log.info(" 终端消息上线下线记录 =  { " + msgId + " } 存入ES成功！");
            } else {
                log.error("终端消息不存在！！！");
            }
        } catch (Exception e) {
            log.error("message : " + message + " 处理异常！", e);
        }

        return null;
    }
}
