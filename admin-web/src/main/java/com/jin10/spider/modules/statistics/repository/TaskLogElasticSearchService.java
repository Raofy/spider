package com.jin10.spider.modules.statistics.repository;

import com.jin10.spider.modules.statistics.bean.TaskLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Airey
 * @date 2019/12/23 17:52
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
public interface TaskLogElasticSearchService extends ElasticsearchRepository<TaskLog, String> {


}
