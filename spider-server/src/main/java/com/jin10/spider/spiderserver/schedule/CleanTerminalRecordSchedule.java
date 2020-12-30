package com.jin10.spider.spiderserver.schedule;

import cn.hutool.core.date.DateUtil;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.global.GlobalCounter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Airey
 * @date 2019/12/12 14:43
 * ----------------------------------------------
 * 定时清除终端上下线记录 每10分钟清理一次
 * ----------------------------------------------
 */
@Component
@Slf4j
public class CleanTerminalRecordSchedule {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;




    /**
     * 从0分钟开始，每10分钟执行一次
     * 下线时间超过24h则被清除掉
     */
    @Scheduled(cron = "20 0/10 * * * ? ")
    public void cleanUp() {

        log.info("==========开始清除超过24小时的终端消息上线下线记录=================");

        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        long time = DateUtil.offsetDay(new Date(), -1).getTime();
        queryBuilder.must(QueryBuilders.rangeQuery("offlineTime").lte(time));
        deleteQuery.setIndex("terminal_record");
        deleteQuery.setType("docs");
        deleteQuery.setQuery(queryBuilder);
        elasticsearchTemplate.delete(deleteQuery);

        log.info("==========清除超过24小时的终端消息上线下线记录成功！！！=================");


    }

    /**
     * 统计消息速率
     * 从40秒开始执行，每分钟执行一次
     */
    @Scheduled(cron = "40 0/1 * * * ? ")
    public void staticsMsgNum() {
        long totalCount = GlobalCounter.term_global_msg_total_count.get();
        long busCount = GlobalCounter.term_global_msg_bus_count.get();
        long fullHttpCount = GlobalCounter.term_global_msg_fullhttp_count.get();
        log.info("消息速率为 : total :  { " + totalCount + " } 个 / min , bus : { " + busCount + " } 个 / min , fullHttp : { " + fullHttpCount + " } 个 / min , 客户端数量为 { " + ChannelSupervise.GlobalGroup.size() + " }个");
        GlobalCounter.term_global_msg_total_count.getAndSet(0L);
        GlobalCounter.term_global_msg_bus_count.getAndSet(0L);
        GlobalCounter.term_global_msg_fullhttp_count.getAndSet(0L);
    }


}
