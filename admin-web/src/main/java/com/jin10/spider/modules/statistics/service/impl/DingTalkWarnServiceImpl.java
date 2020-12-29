package com.jin10.spider.modules.statistics.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.DispatchMsg;
import com.jin10.spider.common.config.CustomConfig;
import com.jin10.spider.common.service.DingTalkClient;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.JsonUtil;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.modules.statistics.bean.*;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.template.entity.Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hongda.fang
 * @date 2019-11-29 10:55
 * ----------------------------------------------
 * 钉钉通知
 */

@Service
public class DingTalkWarnServiceImpl implements IDingTalkWarnService {

    @Autowired
    DingTalkClient client;
    @Autowired
    CustomConfig customConfig;

    private String startStr = "警告：";

    private String dispatchPrefix = "源停止";


    /**
     * 通用消息警告
     *
     * @param msg
     */
    @Override
    public void msgWarn(String msg) {
        String temp = startStr + msg;
        client.sendByNotice(customConfig.getAdminUrl(), temp);
    }

    /**
     * 密钥警告消息
     *
     * @param msg
     */
    @Override
    public void secretKeyMsg(String msg) {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + customConfig.getSecretKey();
        Mac mac;
        String sign = "";
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(customConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = customConfig.secretKeyUrl + "&timestamp=" + timestamp + "&sign=" + sign;
        client.sendByNotice(url, msg);
    }


    /**
     * 任务处理失败次数警告 面板形式
     *
     * @param log
     * @param template
     */
    @Override
    public void taskFailModuleWarn(TaskLog log, Template template) {

        long between = DateUtil.between(template.getFailTime(), new Date(), DateUnit.SECOND);

        DispatchMsg dispatchMsg = new DispatchMsg();
        String title = "源停止警告！";
        String text = dispatchPrefix + "! " + log.getSource() + "/" + template.getChannel()
                + " 最近 " + between + " s内 已经连续" + (template.getRetryTimes() - template.getAllowFailTimes().get()) + " 次执行失败！请检查!";
        dispatchMsg.setTitle(title);
        dispatchMsg.setText(text);
        dispatchMsg.setTargetUrl(template.getPageSite());
        if (!CollectionUtils.isEmpty(log.getSnapshotInfo())) {
//            dispatchMsg.setMonitorUrl("http://115.29.174.245:8880/api/statslog/result/" + template.getId() + "/" + log.getSnapshotInfo().get(0).getSnapshotHash());
        }
        client.sendDispatchMsgStop(customConfig.httpDispatchUrl, dispatchMsg);


    }

    /**
     * 任务处理失败次数警告 markdown形式
     *
     * @param log
     * @param template
     */
    @Override
    public void taskFailMarkDownWarn(TaskLog log, Template template) {

        long between = DateUtil.between(template.getFailTime(), new Date(), DateUnit.SECOND);
        String title = "源停止警告！";
        String text = dispatchPrefix + "! " + log.getSource() + "/" + template.getChannel()
                + " 最近 " + between + " s内 已经连续" + (template.getRetryTimes() - template.getAllowFailTimes().get()) + " 次执行失败！请检查!";

        StringBuffer disMsg = new StringBuffer();
        String monitorUrl = "http://115.29.174.245:8880";
        if (!CollectionUtils.isEmpty(log.getSnapshotInfo())) {
//            monitorUrl = "http://115.29.174.245:8880/api/statslog/result/" + template.getId() + "/" + log.getSnapshotInfo().get(0).getSnapshotHash();
        }
        disMsg.append(text + "\n");
        disMsg.append("\r\n");
        disMsg.append("***\n");
        disMsg.append("[源快照]");
        disMsg.append("(");
        disMsg.append(monitorUrl);
        disMsg.append(")\n");
        disMsg.append("\r\n");
        disMsg.append("***\n");
        disMsg.append("[打开目标页面]");
        disMsg.append("(");
        disMsg.append(template.getPageSite());
        disMsg.append(")");

        client.sendMarkDown(customConfig.httpDispatchUrl, title, disMsg.toString());
    }


    @Override
    public void serverWarn(ServerInfoWarn serverInfoWarn) {
        String msg = null;
        String temp = startStr + "ip {}服务器， 连续 {}min {} 占用超过 {}%，请检查服务器资源\n最后一次上报信息：{}";

        long between = DateUtil.between(new Date(serverInfoWarn.getFirstTime()), new Date(), DateUnit.MINUTE);

        if (Constant.WARN_LIMIT.CPU.equals(serverInfoWarn.getType())) {
            msg = StrUtil.format(temp, serverInfoWarn.getIp(), between, serverInfoWarn.getType(), Constant.WARN_LIMIT.CPU_MAX_TOTAL, JsonUtils2.writeValue(serverInfoWarn.getCpu()));
        } else if (Constant.WARN_LIMIT.MEM.equals(serverInfoWarn.getType())) {
            msg = StrUtil.format(temp, serverInfoWarn.getIp(), between, serverInfoWarn.getType(), Constant.WARN_LIMIT.MEM_MAX_TOTAL, JsonUtils2.writeValue(serverInfoWarn.getMem()));
        } else if (Constant.WARN_LIMIT.FS.equals(serverInfoWarn.getType())) {
            msg = StrUtil.format(temp, serverInfoWarn.getIp(), between, "磁盘", Constant.WARN_LIMIT.FS_MAX_TOTAL, JsonUtils2.writeValue(serverInfoWarn.getServerInfo().getFs()));
        }
        if (StringUtils.isNotBlank(msg)) {
            client.sendByNotice(customConfig.getAdminUrl(), msg);
        }
    }

    @Override
    public void timeOutServerWarn(ServerInfoWarn serverInfoWarn) {
        String temp = startStr + " 服务器ip{} 已经超过 2min 未上报信息，请检查服务器是否被释放， \n 最后一次上报时间：{}, \n 服务信息：{}";
        ServerInfo serverInfo = serverInfoWarn.getServerInfo();
        if (serverInfo != null) {
            String startTimeFormat = DateUtil.format(serverInfo.getCreateTime(), DatePattern.NORM_DATETIME_MINUTE_FORMAT);
            String msg = StrUtil.format(temp, serverInfo.getIp().getPublic_address(), startTimeFormat, JsonUtil.toJson(serverInfo));
            client.sendByNotice(customConfig.getAdminUrl(), msg);
        }
    }


    @Override
    public void taskProductWarn(TaskProductWarn taskProductWarn) {
        String temp = startStr + "爬虫服务未领取爬虫任务， 从{}开始，连续 {}min，{}次未能新增任务，当前还有{}个任务未消费";

        Date startTime = new Date(taskProductWarn.getFirstTime());
        long between = DateUtil.between(startTime, new Date(), DateUnit.MINUTE);
        String startTimeFormat = DateUtil.format(startTime, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
        String msg = StrUtil.format(temp, startTimeFormat, between, taskProductWarn.getTimes(), taskProductWarn.getCurQueueSize());

        client.sendByNotice(customConfig.getAdminUrl(), msg);
    }


    /**
     * 每小时汇报爬虫任务执行统计情况
     *
     * @param item
     */
    @Override
    public void taskLogCountWarn(SpiderRunResultDto item) {

        String temp = startStr + "爬虫服务器 在 {} - {} 共执行调度任务 {} 个,其中成功 {} 个,失败 {} 个 。成功率为 {}%,参与执行的页面源 {} 个！";
        StringBuilder msg = new StringBuilder();
        String successRate = String.format("%.3f", item.getSuccessRate() * 100);
        String format = StrUtil.format(temp, DateUtil.formatDateTime(item.getStartTime()), DateUtil.formatDateTime(item.getEndTime()), item.getAll(), item.getSuccess(), item.getFail(), successRate, item.getTemp());
        msg.append(format);
        msg.append("\r\n");
        msg.append("查看详细请点击: " + " http://cdn.jin10.com/terminal/index.html#/splider/details/" + item.getStartTime().getTime() + "/" + item.getEndTime().getTime());
        client.sendByNotice(customConfig.getAdminUrl(), msg.toString());

    }

    /**
     * 通知维护人员对应的任务情况
     *
     * @param jsonArray
     * @param endTime
     * @param startTime
     */
    @Override
    public void warnMaintainer(JSONArray jsonArray, Date startTime, Date endTime) {


        String dateTemp = "###  {}-{}的任务维护列表\n";
        String dateFormat = StrUtil.format(dateTemp, DateUtil.formatDateTime(startTime), DateUtil.formatTime(endTime));

        StringBuffer msg = new StringBuffer();
        msg.append(dateFormat);
        msg.append("\r\n");
        ArrayList<String> phoneList = new ArrayList<>();
        jsonArray.forEach(item -> {
            JSONObject elem = (JSONObject) item;
            String username = elem.getString("username");
            String phone = elem.getString("phone");
            msg.append("\r\n");
            msg.append("请 ");
            msg.append("[" + username + "]");
            msg.append("(");
            msg.append("http://cdn.jin10.com/terminal/index.html#/splider/details/" + startTime.getTime() + "/" + endTime.getTime() + "/" + username);
            msg.append(") ");
            msg.append(" 关注执行的失败页面!\n");
            msg.append("\r\n");
            phoneList.add(phone);
        });

        String title = "任务警告通知";

        client.sendMarkDown(customConfig.getAdminUrl(), title, msg.toString(), phoneList);
    }

    /**
     * 维护人员上报维护信息
     *
     * @param maintainer
     */
    @Override
    public void warnfinishMaintainTask(String maintainer) {
        String msg = maintainer + " 已完成 " + (DateUtil.month(new Date()) + 1) + "月" + DateUtil.dayOfMonth(new Date()) + "日" + " 终端维护工作!";
        msgWarn(msg);
    }


}
