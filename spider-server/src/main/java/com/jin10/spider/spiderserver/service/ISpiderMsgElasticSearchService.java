package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SpiderMessageElastics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author Airey
 * @date 2019/11/26 16:07
 * ----------------------------------------------
 * spidermessage存储到ElasticSearch中
 * ----------------------------------------------
 */
public interface ISpiderMsgElasticSearchService extends ElasticsearchRepository<SpiderMessageElastics, String> {


    /**
     * 根据标题查询对应的记录
     *
     * @param title
     * @return
     */
    List<SpiderMessageElastics> findByTitle(String title);


    /**
     * 根据msgId查询对应的记录
     *
     * @param msgIdList
     * @return
     */
    List<SpiderMessageElastics> findByMsgIdIn(List<String> msgIdList);

    /**
     *
     * @param msgId
     * @return
     */
    SpiderMessageElastics findByMsgId(String msgId);


}
