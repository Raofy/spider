package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.utils.NettyUtils;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessageElastics;
import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.jin10.spider.spiderserver.service.ISpiderMessageScreenService;
import com.jin10.spider.spiderserver.service.ISpiderMsgElasticSearchService;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Airey
 * @date 2019/12/9 16:09
 * ----------------------------------------------
 * 客户端筛选消息处理
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.SCREEN_MSG)
@Slf4j
public class ScreenMsgServiceImpl implements IActionSocketService {


    @Autowired
    private ISpiderMsgElasticSearchService searchService;

    @Autowired
    private ISpiderMessageScreenService screenService;

    @Autowired
    private NettyUtils nettyUtils;

    /**
     * { "action": 360, "ip": "127.0.0.1", "msgIdList": [ "ec6b9cf8b7ea41afb683b1f9f444d69d", "c2ff9239ea5846af94c2c8ac6702d8fd", "96361229eba94794ad88e78f297e77cb" ] }
     *
     * @param context
     * @param message
     * @return
     */
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);

        JSONObject requestJson = JSONObject.parseObject(message);
        List<String> msgIdList = (List<String>) requestJson.get("msgIdList");
        Long userId = requestJson.getLong("userId");
        String machineCode = requestJson.getString("machineCode");
        if (StringUtils.isBlank(machineCode) || ObjectUtil.isNull(userId)) {
            log.error("传输的报文解析异常！！！");
            return null;
        }

        List<SpiderMessageElastics> resultList = searchService.findByMsgIdIn(msgIdList);
        List<SpiderMessageVO> messageVOList = new ArrayList<>();

        for (SpiderMessageElastics item : resultList) {
            if (DataCache.categoryMap.containsKey(item.getCategory())) {
                item.setCategoryColor((String) DataCache.categoryMap.get(item.getCategory()));
            }
            SpiderMessageScreen messageScreen = new SpiderMessageScreen();
            BeanUtils.copyProperties(item, messageScreen);
            messageScreen.setUserId(userId);
            messageScreen.setInsertTime(null);
            try {
                boolean save = screenService.save(messageScreen);
                if (save) {
                    log.info("数据筛选成功！userId= " + userId + ", msgId = " + messageScreen.getMsgId() + " 入库成功！");
                }
            } catch (DuplicateKeyException e) {
                log.error("数据库已经存在 userId= " + userId + ", msgId = " + messageScreen.getMsgId() + " 的消息记录！！！");
                continue;
            }
            SpiderMessageVO messageVO = new SpiderMessageVO();
            BeanUtils.copyProperties(item, messageVO);
            messageVO.setPushFlag(false);
            messageVOList.add(messageVO);
        }

        if (CollectionUtils.isEmpty(messageVOList)) {
            return null;
        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("list", messageVOList);
        resultJson.put("label", DataCache.labelMap);

        nettyUtils.pushMachineCodeActionChannel(machineCode, MsgCodeEnum.SCREEN, resultJson, true);

        return null;
    }
}
