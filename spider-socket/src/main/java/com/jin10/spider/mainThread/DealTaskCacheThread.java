package com.jin10.spider.mainThread;

import cn.hutool.core.collection.CollUtil;
import com.jin10.spider.bean.UrlTaskDtoPri;
import com.jin10.spider.cache.GlobalCache;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.global.GlobalCounter;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.service.impl.PushTaskMsgServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/4/9 18:29
 * ----------------------------------------------
 * 定时扫描是否有积压的任务需要执行
 * ----------------------------------------------
 */
@Service
@Slf4j
public class DealTaskCacheThread {


    @Autowired
    private PushTaskMsgServiceImpl pushTaskMsgService;

    @Autowired
    private RedisUtils redisUtils;



    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void dealTaskCache() {
        log.info("开始扫描任务缓存！！！");
        if (CollUtil.isEmpty(GlobalCache.taskDtoPrisQueue)) {
            return;
        } else {
            if (CollUtil.isEmpty(ChannelSupervise.timeMap) || redisUtils.hasKey(RedisKey.SPIDER_NODE_ZSET)) {
                log.info("仍然没有足够的机器处理！！！");
                return;
            } else {
                try {
                    for (UrlTaskDtoPri urlTaskDtoPri : GlobalCache.taskDtoPrisQueue) {
                        UrlTaskDtoPri take = GlobalCache.taskDtoPrisQueue.take();
                        pushTaskMsgService.pushMsg(take.getUrlTaskDto());
                    }
                    log.info("缓存任务处理完毕！！！");
                } catch (InterruptedException e) {
                    log.error("获取消息异常！！！", e);
                }
            }
        }
    }


    @Scheduled(cron = "0/10 * * * * ? ")
    public void dealForeignTaskCache() {
        log.info("开始扫描国外任务缓存！！！");
        if (CollUtil.isEmpty(GlobalCache.taskDtoPrisForeignQueue)) {
            return;
        } else {
            if (CollUtil.isEmpty(ChannelSupervise.timeForeignMap) || redisUtils.hasKey(RedisKey.SPIDER_NODE_FOREIGN_ZSET)) {
                log.info("国外服务器！仍然没有足够的机器处理！！！");
                return;
            } else {
                try {
                    for (UrlTaskDtoPri urlTaskDtoPri : GlobalCache.taskDtoPrisForeignQueue) {
                        UrlTaskDtoPri take = GlobalCache.taskDtoPrisForeignQueue.take();
                        pushTaskMsgService.pushMsg(take.getUrlTaskDto(), true);
                    }
                    log.info("国外服务器！缓存任务处理完毕！！！");
                } catch (InterruptedException e) {
                    log.error("国外服务器！获取消息异常！！！", e);
                }
            }
        }

    }


    /**
     * 统计消息速率
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void staticsMsgNum() {
        long totalCount = GlobalCounter.socket_global_msg_total_count.get();
        long busCount = GlobalCounter.socket_global_msg_bus_count.get();
        long fullHttpCount = GlobalCounter.socket_global_msg_fullhttp_count.get();
        log.info("消息速率为 : total :  { " + totalCount + " } 个 / min , bus : { " + busCount + " } 个 / min , fullHttp : { " + fullHttpCount + " } 个 / min , 客户端数量为 { " + ChannelSupervise.GlobalGroup.size() + " }个");
        GlobalCounter.socket_global_msg_total_count.getAndSet(0L);
        GlobalCounter.socket_global_msg_bus_count.getAndSet(0L);
        GlobalCounter.socket_global_msg_fullhttp_count.getAndSet(0L);

    }


}
