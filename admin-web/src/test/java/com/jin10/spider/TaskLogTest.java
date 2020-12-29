package com.jin10.spider;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.repository.TaskLogRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hongda.fang
 * @date 2019-12-10 11:11
 * ----------------------------------------------
 */

@SpringBootTest
public class TaskLogTest {

    @Autowired
    TaskLogRepository taskLogRepository;
    @Autowired
    ElasticsearchTemplate template;


    @Test
    public void test1() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("ip")
                        .subAggregation(AggregationBuilders.count("priceAvg").field("tempId")


                        ) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<TaskLog> aggPage = (AggregatedPage<TaskLog>) this.taskLogRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");

        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
 /*       for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) stringAggregationMap.get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());

            Aggregation tempIdNum = stringAggregationMap.get("priceAvgs");


            System.out.println(tempIdNum);

        }*/


        Terms userAgg = aggPage.getAggregations().get("brands");
        for (Terms.Bucket entry : userAgg.getBuckets()) {
            InternalAvg sexAgg = entry.getAggregations().get("priceAvg");
            String a = "";
//            for (Terms.Bucket entry2 : sexAgg.getMetaData()) {
//                Sum sum = entry2.getAggregations().get("priceAvgs");
//                System.out.println("user:" + entry.getKey()+"-------sex:"+entry2.getKey() + "----------sum:" + sum.getValue());
//            }
        }


    }


    @Test
    public void testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("ip"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<TaskLog> aggPage = (AggregatedPage<TaskLog>) this.taskLogRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }


    @Test
    public void testResult() {

        Date endTime = new Date();
        Date startTime = DateUtil.offsetMinute(endTime, -200);


        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(startTime.getTime()).lte(endTime.getTime()));

        TermsAggregationBuilder field = AggregationBuilders.terms("spiders").field("ip");

        ValueCountAggregationBuilder sumBuilder = AggregationBuilders.count("tempIdSum").field("tempId");
        SumAggregationBuilder statusBuilder = AggregationBuilders.sum("statusSum").field("status");
        FilterAggregationBuilder filterAggregationBuilder = AggregationBuilders.filter("status", QueryBuilders.queryStringQuery("status:0"))
                .subAggregation(AggregationBuilders.count("filter").field("status"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(sumBuilder)
                .addAggregation(statusBuilder)
                .addAggregation(filterAggregationBuilder)
                .build();
//        AggregatedPage<TaskLog> search = (AggregatedPage<TaskLog>) taskLogRepository.search(searchQuery);


        Aggregations query = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        String a = "";

        double saleAmount = template.query(searchQuery, response -> {
            InternalSum sum = (InternalSum) response.getAggregations().asList().get(0);

            Map<String, Aggregation> stringAggregationMap = response.getAggregations().asMap();

            for (String key : stringAggregationMap.keySet()) {
                stringAggregationMap.get(key);

                System.out.println(stringAggregationMap.get(key));
            }
            return sum.getValue();
        });

        System.out.println("a");


//        double saleAmount = taskLogRepository.search(searchQuery, response -> {
//            InternalSum sum = (InternalSum)response.getAggregation().asList().get(0);
//            return sum.getValue();
//        });


//        ArrayList<TaskLog> taskLogs = Lists.newArrayList(search);
//        System.out.println(taskLogs.size());

    }


    @Test
    public void test3() {


        Date endTime = new Date();
        Date startTime = DateUtil.offsetMinute(endTime, -200);


        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(startTime.getTime()).lte(endTime.getTime()));

        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("spiderResult").field("ip");

        ValueCountAggregationBuilder tempNumAgg = AggregationBuilders.count("tempNum").field("tempId");

        FilterAggregationBuilder successFilterAgg = AggregationBuilders.filter("successNum", QueryBuilders.queryStringQuery("status:0"))
                .subAggregation(AggregationBuilders.count("successNum").field("status"));


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(teamAgg.subAggregation(tempNumAgg).subAggregation(successFilterAgg))
                .build();

        Aggregations aggregations = template.query(searchQuery,
                new ResultsExtractor<Aggregations>() {
                    @Override
                    public Aggregations extract(SearchResponse response) {
                        return response.getAggregations();
                    }
                });

        StringTerms resultTerms = (StringTerms )aggregations.asMap().get("spiderResult");
        List<StringTerms.Bucket> buckets = resultTerms.getBuckets();

        buckets.forEach(bucket -> {
           String ip =  bucket.getKeyAsString();
            long docCount = bucket.getDocCount();

            Aggregations subAgg = bucket.getAggregations();
            if (subAgg != null){
                Map<String, Aggregation> stringAggregationMap = subAgg.asMap();

                InternalValueCount runNum = (InternalValueCount) stringAggregationMap.get("tempNum");

                InternalFilter internalFilter = (InternalFilter) stringAggregationMap.get("successNum");
                InternalValueCount successNum = (InternalValueCount) internalFilter.getAggregations().asMap().get("successNum");

                String format = "ip:{}, docCount:{}, runNum: {}, success {} ";

                System.out.println(  StrUtil.format(format, ip, docCount, runNum.getValue(), successNum));
            }
        });

    }


    @Test
    public void test5(){

        Date endTime = new Date();
        Date startTime = DateUtil.offsetMinute(endTime, -200);


        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(startTime.getTime()).lte(endTime.getTime()));


        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("spider").field("ip");
        ValueCountAggregationBuilder tempNumAgg  = AggregationBuilders.count("tempNum").field("tempId");
        CardinalityAggregationBuilder runNumAgg = AggregationBuilders.cardinality("runNum").field("taskId");

        ValueCountAggregationBuilder ageAgg = AggregationBuilders.count("avg_age").field("status");



        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(teamAgg.subAggregation(tempNumAgg).subAggregation(runNumAgg))
                .build();

        template.query(searchQuery, response -> {


            StringTerms terms = (StringTerms) response.getAggregations().asList().get(0);

            List<StringTerms.Bucket> buckets = terms.getBuckets();

            buckets.forEach(bucket -> {

                Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();

                String a = "";

            });





            return 0.1;
        });
    }





    @Test
    public void test6(){

        Date endTime = new Date();
        Date startTime = DateUtil.offsetMinute(endTime, -200);


        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(startTime.getTime()).lte(endTime.getTime()));


        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("team").field("ip");
        ValueCountAggregationBuilder ageAgg = AggregationBuilders.count("avg_age").field("status");
        ValueCountAggregationBuilder salaryAgg  = AggregationBuilders.count("total_salary ").field("tempId");


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(teamAgg.subAggregation(ageAgg).subAggregation(salaryAgg))
                .build();

        template.query(searchQuery, response -> {
            InternalAvg sum = (InternalAvg) response.getAggregations().asList().get(0);

            Map<String, Aggregation> stringAggregationMap = response.getAggregations().asMap();

            for (String key : stringAggregationMap.keySet()) {
                stringAggregationMap.get(key);

                System.out.println(stringAggregationMap.get(key));
            }
            return sum.getValue();
        });
    }


}
