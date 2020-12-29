package com.jin10.spider.spiderserver.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.service.DingTalkClient;
import com.jin10.spider.common.trans.TransApi;
import com.jin10.spider.common.trans.TransResponse;
import com.jin10.spider.common.utils.ChineseUtils;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.common.utils.NettyUtils;
import com.jin10.spider.spiderserver.bean.Item;
import com.jin10.spider.spiderserver.config.CustomConfig;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.*;
import com.jin10.spider.spiderserver.enums.MessageStaus;
import com.jin10.spider.spiderserver.service.*;
import com.jin10.spider.spiderserver.utils.HttpClientUtils;
import com.jin10.spider.spiderserver.utils.MD5Util;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.jin10.spider.spiderserver.constants.GlobalConstants.*;


/**
 * @author Airey
 * @date 2019/11/13 15:39
 * ----------------------------------------------
 * 从rabbitmq中接收消息并处理
 * 1.转发到websocket
 * 2.存入ElasticSearch中
 * 3.数据入库
 * 4.更新消息最后更新时间状态
 * ----------------------------------------------
 */
@Component
@Slf4j
public class MqReceiver {


    @Autowired
    private ThreadPoolTaskExecutor asyncMq;

    @Autowired
    private ISpiderMessageService messageService;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ISpiderMessageAbnormalService abnormalService;

    @Autowired
    private DingTalkClient dingTalkClient;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private ISpiderMessageMonitorService monitorService;

    @Autowired
    private ISpiderCategoryService categoryService;

    @Autowired
    private NettyUtils nettyUtils;

    @Autowired
    private ISpiderMessagePushService pushService;

    /**
     * 百度翻译 app
     */
    @Value("${baidu.trans.appId}")
    private String appId;

    /**
     * 百度翻译密码
     */
    @Value("${baidu.trans.securityKey}")
    private String securityKey;


    /**
     * 消息处理逻辑
     * 转发到客户端，然后入库
     *
     * @param message
     */
    public void dealMsg(String message) {
        log.info("开始处理消息 ===> " + message);
        long startTime = System.currentTimeMillis();
        if (!JSONUtil.isJson(message)) {
            log.error("msg 不符合JSON规范,请检查消息格式！！！");
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(message);
            log.info("json解析时间=== " + (System.currentTimeMillis() - startTime) + " /ms");
            String category = jsonObject.getString("category");
            String channel = jsonObject.getString("channel");
            String source = jsonObject.getString("source");
            //校验消息类别是否合法
            verifyCategory(category, message);
            //组装json
            processResJson(jsonObject, category);
            List<SpiderMessage> spiderMessageList = new ArrayList<>();
            List<SpiderMessageVO> messageVOList = new ArrayList<>();
                List<SpiderMessageElastics> elasticsList = new ArrayList<>();
            log.info("消息组装和校验完毕! " + (System.currentTimeMillis() - startTime) + " /ms");
            JSONArray data = jsonObject.getJSONArray("data");
            data.forEach(item -> {
                String trans = null;
                SpiderMessage spiderMessage = new SpiderMessage();
                spiderMessage.setTaskId(jsonObject.getString("taskId"));
                spiderMessage.setMsgId(((JSONObject) item).getString("msgId"));
                spiderMessage.setTime(DateUtil.date((Long) jsonObject.get("time")));
                spiderMessage.setTitle(((JSONObject) item).getString("title"));
                //标题不包含中文，使用百度翻译
                if(!ChineseUtils.isContainChinese(spiderMessage.getTitle())){
                    trans = trans(spiderMessage.getTitle());
                    String title = spiderMessage.getTitle() + "<br/>" + trans;
                    spiderMessage.setTitle(title);
                }
                spiderMessage.setUrl(((JSONObject) item).getString("url"));
                spiderMessage.setSource(source);
                spiderMessage.setCategory(category);
                spiderMessage.setTempId(jsonObject.getInteger("tempId"));
                spiderMessage.setChannel(channel);
                spiderMessage.setDeptId(jsonObject.getInteger("deptId"));
                spiderMessage.setRemark(jsonObject.getString("remark"));
                SpiderMessageElastics elastics = new SpiderMessageElastics();
                SpiderMessageVO messageVO = new SpiderMessageVO();
                BeanUtils.copyProperties(spiderMessage, messageVO);
                messageVO.setCategoryColor(jsonObject.getString("categoryColor"));
                joinCacheQueue(messageVO);
                BeanUtils.copyProperties(spiderMessage, elastics);
                elastics.setInsertTime(new Date());
                messageVOList.add(messageVO);
                elasticsList.add(elastics);
                spiderMessageList.add(spiderMessage);
            });

            log.info("组装各种对象完毕！ " + (System.currentTimeMillis() - startTime) + " /ms");
            JSONObject msgJson = new JSONObject();
            msgJson.put("list", messageVOList);
            msgJson.put("label", DataCache.labelMap);
            //判断是否直接推送给推送台
            if (DATA_CENTER.equals(category) || PEOPLE_BANK_OF_CHINA.equals(source) || DataCache.autoPushMap.containsKey(MD5Util.getMessageMonitorCode(source, category, channel))) {
                log.info("开始自动推送消息 == 》 category={},source={},channel={} and md5={}", category, source, channel, MD5Util.getMessageMonitorCode(source, category, channel));
                msgJson.put("env", "prod");
                if (CollUtil.isNotEmpty(elasticsList)) {
                    for (SpiderMessageElastics item : elasticsList) {
                        SpiderMessagePush messagePush = new SpiderMessagePush();
                        BeanUtils.copyProperties(item, messagePush);
                        messagePush.setInsertTime(null);
                        messagePush.setEnv("prod");
                        //异步存入数据库
                        asyncMq.submit(() ->{
                            try {
                                boolean save = pushService.save(messagePush);
                                if (save) {
                                    log.info("数据保存成功！ msgId = " + messagePush.getMsgId() + " 入库成功！");
                                }
                            } catch (DuplicateKeyException e) {
                                log.error("数据库已经存在 msgId = " + messagePush.getMsgId() + " 的消息记录！！！");
                            }
                        });
                    }
                }
                //推送台
                nettyUtils.pushRegisterActionChannel(ActionCodeConstants.PUSH_MSG_GROUP, MsgCodeEnum.PUSH, msgJson, true);
            }
            //控制台
            nettyUtils.pushRegisterActionChannel(ActionCodeConstants.NORMAL_MSG_GROUP, MsgCodeEnum.NORMAL, msgJson, true);
            log.info("转发消息到客户端成功！！！ " + (System.currentTimeMillis() - startTime) + " /ms");
            //口袋贵金属，多付空，空付多，推送给交易侠
            if(source.contains("口袋贵金属") && "app推送".equals(category) && "第三方推送快讯".equals(channel)){
                if(jsonObject.containsKey("title")){
                    String title = jsonObject.getString("title");
                    if(title.contains("递延方向及结算价")){
                        //异步通知交易侠
                        asyncMq.submit(() -> notifyTrader(title));
                    }
                }
            }
            //异步
            asyncMq.submit(() ->{
                messageService.insertBatch(spiderMessageList);
                updateMessageMonitor(spiderMessageList);
                log.info("更新消息状态监控成功！！！  " + (System.currentTimeMillis() - startTime) + " /ms");
                saveBatchForElasticsearch(elasticsList, startTime);
            });

        } catch (Exception e) {
            log.error("消息处理异常:  ", e);
        }


    }

    /**
     * 通知交易侠
     * @param title
     */
    public void notifyTrader(String title) {
        //转小写
        String text = title.toLowerCase();

        List<Item> sorts = new ArrayList<>();

        sorts.add(new Item(text.indexOf("ag"), "ag"));
        text = text.replaceFirst("ag", "嘄嘄");

        sorts.add(new Item(text.indexOf("mau"), "mau"));
        text = text.replaceFirst("mau", "嘄嘄嘄");

        sorts.add(new Item(text.indexOf("au"), "au"));
        text = text.replaceFirst("au", "嘄嘄");


        Collections.sort(sorts, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        String[] items = text.split("嘄嘄");

        String agtd = null;
        String autd = null;
        String mautd = null;

        for(int i = 0; i < sorts.size(); i++){
            String key = sorts.get(i).getKey();
            String str = items[i + 1].contains("多付空") ? "1" : "2";
            if("ag".equals(key)){
                agtd = str;
            }else if ("au".equals(key)){
                autd = str;
            }else {
                mautd = str;
            }
            log.info("{} : {}",key,str);
        }


        Map<String, String> params = new HashMap<>(3);
        params.put("agtd",agtd);
        params.put("autd",autd);
        params.put("mautd",mautd);

        String result = HttpClientUtils.doGetRequest("http://101.37.34.92:3500/payDir", null, params);
        if("push pay direction success!".equals(result)){
            log.info("通知交易侠成功------------------");
        }else {
            log.info("通知交易侠失败------------------");
        }
        String toJSONString = JSONObject.toJSONString(params);
        StringEntity stringEntity = new StringEntity(toJSONString, ContentType.APPLICATION_JSON);
        Map<String,String> heads = new HashMap<>();
        heads.put("x-version","admin");
        String postResponse = HttpClientUtils.doPostRequest("https://mock-trading-api.jin10.com/symbolRecord/payDir", heads, null,
                stringEntity);
        log.info("通知模拟交易结果 = {}",postResponse);

    }

    /**
     * 百度翻译
     * 翻译失败返回原文
     * @param query 要翻译的文章
     */
    public String trans(String query){
        TransApi transApi = new TransApi(appId,securityKey);
        String result = transApi.getTransResult(query, "auto", "zh");
        log.info("翻译结果 = {}",result);
        if(StringUtils.isNotBlank(result)){
            TransResponse response = JsonUtils2.readValue(result, TransResponse.class);
            return response.getTransResult().get(0).getDst();
        }
        return query;
    }

    /**
     * 存储ES
     *
     * @param elasticsList
     * @param startTime
     */
    private void saveBatchForElasticsearch(List<SpiderMessageElastics> elasticsList, Long startTime) {
        log.info("准备存储ES! " + (System.currentTimeMillis() - startTime) + " /ms");
        List<IndexQuery> indexQueries = new ArrayList<>();
        elasticsList.forEach(item -> {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(item.getMsgId());
            indexQuery.setSource(JSON.toJSONString(item));
            indexQuery.setIndexName("spider_message");
            indexQuery.setType("docs");
            indexQueries.add(indexQuery);
        });
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh("spider_message");
        log.info("消息列表存入ElasticSearch成功！" + (System.currentTimeMillis() - startTime) + " /ms");
    }


    /**
     * 校验消息类别是否合法
     *
     * @param category
     * @param message
     */
    private void verifyCategory(String category, String message) {

        if (StringUtils.isBlank(category)) {
            log.error("从爬虫端接收到的msg中不包含category,不予转发消息！请检查关键字段！");
            SpiderMessageAbnormal abnormal = new SpiderMessageAbnormal(MessageStaus.NULL_CATEGORY, message);
            String content = "警告！ " + DINGDING_PREFIX + " ,消息类型为空！ 请检查! " + message;
            List<String> phoneList = customConfig.getReceiver();
            dingTalkClient.sendByReceiver(customConfig.getHttpUrl(), content, phoneList);
            abnormalService.save(abnormal);
            return;
        }

        if (!DataCache.categoryMap.containsKey(category)) {
            SpiderCategory spiderCategory = categoryService.selectOne(category);
            if (ObjectUtil.isNull(spiderCategory)) {
                log.error("从爬虫端接收到的msg中发现未知的category,不予转发消息！请检查消息来源！");
                SpiderMessageAbnormal abnormal = new SpiderMessageAbnormal(MessageStaus.ILLEGAL_CATEGROY, message);
                String content = "警告！ " + DINGDING_PREFIX + " , 非法的消息类型 [ " + category + " ] ,请检查！" + message;
                dingTalkClient.sendByAll(customConfig.getHttpUrl(), content);
                abnormalService.save(abnormal);
                return;
            } else {
                DataCache.categoryMap.put(spiderCategory.getCategoryName(), spiderCategory.getCategoryColor());
            }
        }

    }


    /**
     * 处理json Message
     * <p>
     * 1.添加唯一msgId
     * 2.添加标签库label
     *
     * @param msgJson
     */
    private void processResJson(JSONObject msgJson, String category) {

        JSONArray data = new JSONArray();
        if (msgJson.containsKey("data")) {
            data = msgJson.getJSONArray("data");
            for (Object item : data) {
                if (item instanceof JSONObject) {
                    ((JSONObject) item).put("msgId", IdUtil.fastSimpleUUID());
                }
            }
        } else {

        }
        msgJson.put("data", data);
        msgJson.put("label", DataCache.labelMap);
        msgJson.put("categoryColor", DataCache.categoryMap.get(category));
    }


    /**
     * 将最新的消息放入缓存队列中
     *
     * @param messageVO
     */
    private void joinCacheQueue(SpiderMessageVO messageVO) {
        if (DataCache.SpiderMessageQueue.offer(messageVO)) {

        } else {
            DataCache.SpiderMessageQueue.poll();
            DataCache.SpiderMessageQueue.offer(messageVO);
        }
    }

    /**
     * 更新消息分类监控状态
     *
     * @param spiderMessageList
     */
    private void updateMessageMonitor(List<SpiderMessage> spiderMessageList) {

        spiderMessageList.forEach(
                e -> {
                    int dispatch;
                    if (ObjectUtil.isNotNull(e.getTempId())) {
                        dispatch = 0;
                    } else {
                        dispatch = 1;
                    }
                    SpiderMessageMonitor monitor = new SpiderMessageMonitor();
                    monitor.setCategory(e.getCategory());
                    monitor.setSource(e.getSource());
                    monitor.setChannel(e.getChannel());
                    monitor.setDispatch(dispatch);
                    monitor.setUpdateTime(e.getTime());
                    monitor.setTempId(e.getTempId());
                    monitorService.updateOrInsertRecord(monitor);
                }
        );


    }

    public static void main(String[] args) {
        TransApi transApi = new TransApi("20200411000416443","3ZS237_k_zfBFjc7kwxU");
        String result = transApi.getTransResult("this is test", "auto", "zh");
        System.out.println("翻译结果 = " +result);
        if(StringUtils.isNotBlank(result)){
            TransResponse response = JsonUtils2.readValue(result, TransResponse.class);
            String transResultDst = response.getTransResult().get(0).getDst();
            System.out.println("翻译结果 = " + transResultDst);
        }else {
            System.out.println("error ===============");
        }

    }


}
