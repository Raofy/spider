package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.date.DateUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;

import java.util.Date;

/**
 * @author Airey
 * @date 2019/12/19 10:17
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class ElasticsearchDeleteTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void delete(){

        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        long time = System.currentTimeMillis();
        queryBuilder.must(QueryBuilders.rangeQuery("creatTime").lte(time));
        deleteQuery.setIndex("task_log");
        deleteQuery.setType("logs");
        deleteQuery.setQuery(queryBuilder);
        elasticsearchTemplate.delete(deleteQuery);



    }


}
