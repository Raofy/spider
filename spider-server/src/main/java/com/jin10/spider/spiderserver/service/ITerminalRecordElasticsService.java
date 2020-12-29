package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.TerminalRecordElastics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Airey
 * @date 2019/12/11 17:42
 * ----------------------------------------------
 * 终端消息上线下线记录 es处理类
 * ----------------------------------------------
 */
public interface ITerminalRecordElasticsService extends ElasticsearchRepository<TerminalRecordElastics, String> {


}
