package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessage;
import com.jin10.spider.spiderserver.mapper.SpiderMessageMapper;
import com.jin10.spider.spiderserver.service.ISpiderMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.jin10.spider.spiderserver.constants.GlobalConstants.CACHE_QUEUE_SIZE;

/**
 * <p>
 * 爬虫消息列表 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-18
 */
@Service
@Slf4j
public class SpiderMessageServiceImpl extends ServiceImpl<SpiderMessageMapper, SpiderMessage> implements ISpiderMessageService {


    /**
     * 批量插入数据库
     *
     * @param spiderMessageList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(List<SpiderMessage> spiderMessageList) {
        boolean b = this.saveBatch(spiderMessageList);
        if (b) {
            log.info("消息列表入库成功! ");
        } else {
            log.info("消息列表入库失败!");
        }
        return b;
    }

    /**
     * 插入单条数据库记录
     *
     * @param spiderMessage
     * @return
     */
    @Override
    public boolean insert(SpiderMessage spiderMessage) {
        return this.save(spiderMessage);
    }

    /**
     * 获取最新的size条数据
     *
     * @return
     */
    @Override
    public JSONObject findLatestMessageList() {
        JSONObject resultJson = new JSONObject();
        resultJson.put("label", DataCache.labelMap);
        LinkedBlockingQueue<Object> resultQueue = new LinkedBlockingQueue<>(CACHE_QUEUE_SIZE);
        PriorityBlockingQueue<SpiderMessageVO> spiderMessageQueue = new PriorityBlockingQueue<>(DataCache.SpiderMessageQueue); // 缓存最新200条爬虫消息
        if (spiderMessageQueue.size() < CACHE_QUEUE_SIZE || CollectionUtils.isEmpty(spiderMessageQueue)) {
            log.info("消息队列未满,从数据库中查询最新 { " + CACHE_QUEUE_SIZE + " } 条消息");

            List<SpiderMessage> latestMessageList = baseMapper.findLatestMessageList(getTableName(new Date()), CACHE_QUEUE_SIZE);
            latestMessageList.forEach(x -> {
                if (DataCache.categoryMap.containsKey(x.getCategory())) {
                    x.setCategoryColor((String) DataCache.categoryMap.get(x.getCategory()));
                }
            });
            resultJson.put("list", latestMessageList);
        } else {
            log.info("从内存消息队列中查询最新 { " + CACHE_QUEUE_SIZE + " } 条消息");
            try {
                for (SpiderMessageVO messageVO : spiderMessageQueue) {
                    SpiderMessageVO take = spiderMessageQueue.take();
                    if (DataCache.categoryMap.containsKey(take.getCategory())) {
                        take.setCategoryColor((String) DataCache.categoryMap.get(take.getCategory()));
                    }
                    resultQueue.offer(take);
                }
                resultJson.put("list", resultQueue);
            } catch (InterruptedException e) {
                log.error("缓存消息队列解析消息异常！ ", e);
            }
        }

        return resultJson;
    }

    /**
     * 查询需要返回给前端的补偿消息
     *
     * @param time
     * @return
     */
    @Override
    public List<SpiderMessage> findCompleteMessageList(Long time) {

        List<SpiderMessage> spiderMessageList = baseMapper.findCompleteMessageList(getTableName(DateUtil.date(time)), time / 1000, System.currentTimeMillis() / 1000);
        return spiderMessageList;
    }


    /**
     * 根据时间获取需要查询的表名 spider_message_2019_1
     *
     * @param date
     * @return
     */
    private String getTableName(Date date) {
        String spilt = "_";
        StringBuffer tableName = new StringBuffer("spider_message");
        int year = DateUtil.year(date);
        tableName.append(spilt).append(year);
        int month = DateUtil.month(date) + 1;
        if (month <= 6) {
            tableName.append(spilt).append(0);
        } else {
            tableName.append(spilt).append(1);
        }
        return tableName.toString();
    }


}
