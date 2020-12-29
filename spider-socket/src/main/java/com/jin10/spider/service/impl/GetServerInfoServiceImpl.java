package com.jin10.spider.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.utils.NettyChannelUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author Airey
 * @date 2020/2/7 15:07
 * ----------------------------------------------
 * 推送服务器信息
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.GET_SERVER_INFOS)
@Slf4j
public class GetServerInfoServiceImpl implements IActionSocketService {


    @Autowired
    private NettyChannelUtils nettyChannelUtils;

    /**
     * 爬虫调度端地址
     */
    @Value("${custom.admin.httpUrl}")
    public String adminUrl;

    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        JSONObject jsonObj = JSONObject.parseObject(message);
        if (jsonObj.containsKey("clientMsg")) {
            String clientMsg = jsonObj.getString("clientMsg");
            nettyChannelUtils.pushRegisterActionChannel(ActionCodeConstants.GET_SERVER_INFOS, clientMsg);
            return null;
        } else {
            ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.GET_SERVER_INFOS);
            String realUrl = adminUrl + "/admin/taskLog/getServerInfos";
            JSONArray data = new JSONArray();
            HttpResponse execute = HttpRequest.get(realUrl).execute();
            if (execute.isOk()) {
                String body = execute.body();
                if (JSONUtil.isJson(body)) {
                    JSONObject serverJson = JSONObject.parseObject(body);
                    data = serverJson.getJSONArray("data");
                }
            }
            MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.SERVER_INFOS, data);
            return msgResponse;
        }

    }
}
