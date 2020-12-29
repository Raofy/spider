package com.jin10.spider;

import com.jin10.spider.modules.statistics.bean.TaskLog;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Airey
 * @date 2019/12/10 17:39
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class AggregateQueryTest {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 聚合测试
     *
     * 查询12月5日的模板所产生的任务数量
     *
     * {1=518, 2=554, 4=553, 6=556, 7=520, 8=557, 9=517, 10=557, 11=517, 13=556}
     *
     */
    @Test
    public void testAggregate() {

        String aggName = "popular";

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.rangeQuery("creatTime").gte(1575475200000L).lte(1575561540000L));
        queryBuilder.addAggregation(AggregationBuilders.terms(aggName).field("tempId"));

        AggregatedPage<TaskLog> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), TaskLog.class);

        Aggregations aggregations = result.getAggregations();

        LongTerms terms = aggregations.get(aggName);

        List<LongTerms.Bucket> buckets = terms.getBuckets();

        Map<Long, Integer> map = new HashMap<>();

        buckets.forEach(item -> {
            map.put((Long) item.getKeyAsNumber(), (int) item.getDocCount());
        });

        System.out.println(map);

    }


}
