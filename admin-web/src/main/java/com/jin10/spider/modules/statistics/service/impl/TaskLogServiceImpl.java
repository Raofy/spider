package com.jin10.spider.modules.statistics.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.repository.BaseEsRepository;
import com.jin10.spider.common.service.BaseEsService;
import com.jin10.spider.common.utils.*;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.dto.ProxyResultDto;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.statistics.dto.TemplateResultDto;
import com.jin10.spider.modules.statistics.repository.TaskLogElasticSearchService;
import com.jin10.spider.modules.statistics.repository.TaskLogRepository;
import com.jin10.spider.modules.statistics.request.TaskLogPageRequest;
import com.jin10.spider.modules.statistics.service.ITaskLogService;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author hongda.fang
 * @date 2019-11-26 17:39
 * ----------------------------------------------
 */
@Service
public class TaskLogServiceImpl extends BaseEsService<TaskLog, String> implements ITaskLogService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskLogRepository taskLogRepository;
    @Autowired
    ElasticsearchTemplate template;
    @Autowired
    private TaskLogElasticSearchService searchService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    protected BaseEsRepository<TaskLog, String> getDao() {
        return taskLogRepository;
    }

    @Override
    public TaskLog findByTaskId(String taskId) {
        Optional<TaskLog> byId = taskLogRepository.findById(taskId);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    @Override
    public PageUtils queryPage(TaskLogPageRequest pageRequest) {
        logger.info(" task log  queryPage : " + JsonUtils2.writeValue(pageRequest));
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.rangeQuery("creatTime").gte(pageRequest.getStartTime()).lte(pageRequest.getEndTime()));

        if (StringUtils.isNotBlank(pageRequest.getTaskId())) {
            queryBuilder.must(QueryBuilders.termQuery("taskId",pageRequest.getTaskId()));
        }


        if (StringUtils.isNotBlank(pageRequest.getIp())) {
            queryBuilder.must(QueryBuilders.termQuery("ip", pageRequest.getIp()));
        }
        if (pageRequest.getTempId() != null) {
            queryBuilder.must(QueryBuilders.termQuery("tempId", pageRequest.getTempId()));
        }

        if (StringUtils.isNotBlank(pageRequest.getProxy())) {
            queryBuilder.must(QueryBuilders.termQuery("proxy", pageRequest.getProxy()));
        }

        if (StringUtils.isNotBlank(pageRequest.getProxyPort())) {
            queryBuilder.must(QueryBuilders.termQuery("proxyPort", pageRequest.getProxyPort()));
        }

        if (StringUtils.isNotEmpty(pageRequest.getProxyArea())) {
            queryBuilder.must(QueryBuilders.matchQuery("proxyArea", pageRequest.getProxyArea()));
        }

        if (StringUtils.isNotBlank(pageRequest.getSource())) {
            queryBuilder.must(QueryBuilders.matchQuery("source", pageRequest.getSource()));
        }

        if (StringUtils.isNotBlank(pageRequest.getMaintainer())) {
            String maintainer = pageRequest.getMaintainer();
            Object tempIdRedisSet = redisUtils.hget(RedisKey.MAINTAINER_MAP, maintainer);
            if (ObjectUtil.isNotNull(tempIdRedisSet) && tempIdRedisSet instanceof Set) {
                Set<Integer> tempIdSet = (Set<Integer>) tempIdRedisSet;
                queryBuilder.must(QueryBuilders.termsQuery("tempId", tempIdSet));
            }
        }

        if (ObjectUtil.isNotNull(pageRequest.getStatus())) {
            if (!pageRequest.getStatus().equals(Constant.TASK_LOG_STATUS.SUCCESS)) {
                queryBuilder.mustNot(QueryBuilders.matchQuery("status", Constant.TASK_LOG_STATUS.SUCCESS));
            } else {
                queryBuilder.must(QueryBuilders.matchQuery("status", Constant.TASK_LOG_STATUS.SUCCESS));
            }
        }

        searchQuery.withQuery(queryBuilder);
        searchQuery.withPageable(QueryEsPage.getEsPage(pageRequest));
        Page<TaskLog> search1 = searchService.search(searchQuery.build());
        return new PageUtils(search1);
    }

    @Override
    public boolean deleteLog(Date time) {
        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Map<Object, Object> tempIdMap = redisUtils.hmget(RedisKey.TEMPIDMAP);
        Set<Object> tempIdSet = tempIdMap.keySet();
        queryBuilder.must(QueryBuilders.termsQuery("tempId", tempIdSet));
        queryBuilder.must(QueryBuilders.rangeQuery("creatTime").lte(time.getTime()));
        deleteQuery.setQuery(queryBuilder);
        template.delete(deleteQuery, TaskLog.class);
        return false;
    }


    @Override
    public boolean deleteStopTemplateLog(Date time) {
        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Map<Object, Object> tempIdMap = redisUtils.hmget(RedisKey.TEMPIDMAP);
        Set<Object> tempIdSet = tempIdMap.keySet();
        queryBuilder.mustNot(QueryBuilders.termsQuery("tempId", tempIdSet));
        queryBuilder.must(QueryBuilders.rangeQuery("creatTime").lte(time.getTime()));
        deleteQuery.setQuery(queryBuilder);
        template.delete(deleteQuery, TaskLog.class);
        return false;
    }

    @Override
    public List<SpiderRunResultDto> countSpiderRunResult(TaskLogPageRequest request) {

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(request.getStartTime()).lte(request.getEndTime()))
                .must(QueryBuilders.termQuery("puted", true));
        //集合查询
        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("spiderResult").field("ip.keyword");
        //模板数量--聚合查询
        CardinalityAggregationBuilder tempNumAgg = AggregationBuilders.cardinality("tempNum").field("tempId");

        FilterAggregationBuilder successFilterAgg = AggregationBuilders.filter("successNum", QueryBuilders.queryStringQuery("status:2"))
                .subAggregation(AggregationBuilders.count("successNum").field("status"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(teamAgg.subAggregation(tempNumAgg).subAggregation(successFilterAgg))
                .build();

        AggregatedPage<TaskLog> taskLogsPage = template.queryForPage(searchQuery, TaskLog.class);


        Aggregations aggregations = taskLogsPage.getAggregations();

        StringTerms resultTerms = (StringTerms) aggregations.asMap().get("spiderResult");
        List<StringTerms.Bucket> buckets = resultTerms.getBuckets();

        List<SpiderRunResultDto> runResultDtos = new ArrayList<>();

        buckets.forEach(bucket -> {
            String ip = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();

            logger.info("ip:{}, count:{}", ip, docCount);
            Aggregations subAgg = bucket.getAggregations();
            if (subAgg != null) {
                Map<String, Aggregation> stringAggregationMap = subAgg.asMap();

                InternalCardinality tempNum = (InternalCardinality) stringAggregationMap.get("tempNum");
                InternalFilter internalFilter = (InternalFilter) stringAggregationMap.get("successNum");
                InternalValueCount successNum = (InternalValueCount) internalFilter.getAggregations().asMap().get("successNum");

                SpiderRunResultDto runResultDto = new SpiderRunResultDto();
                runResultDto.setIp(ip);
                runResultDto.setAll(docCount);
                runResultDto.setTemp(tempNum.getValue());
                runResultDto.setSuccess(successNum.getValue());
                String successCount = successNum.getValueAsString();
                String allCount = String.valueOf(docCount);
                runResultDto.setSuccessRate(NumberUtil.div(successCount, allCount, 5).doubleValue());
                runResultDto.setFail(docCount - successNum.getValue());
                runResultDto.setStartTime(new Date(request.getStartTime()));
                runResultDto.setEndTime(new Date(request.getEndTime()));
                runResultDtos.add(runResultDto);
            }
        });
        return runResultDtos;
    }

    @Override
    public List<ProxyResultDto> countProxy(TaskLogPageRequest request) {


        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("creatTime")
                        .gte(request.getStartTime()).lte(request.getEndTime()));

        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("proxy").field("proxy.keyword");
        FilterAggregationBuilder successFilterAgg = AggregationBuilders.filter("successNum", QueryBuilders.queryStringQuery("status:2"))
                .subAggregation(AggregationBuilders.count("successNum").field("status"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .addAggregation(teamAgg.subAggregation(successFilterAgg))
                .build();

        AggregatedPage<TaskLog> taskLogsPage = template.queryForPage(searchQuery, TaskLog.class);

        Aggregations aggregations = taskLogsPage.getAggregations();

        if (aggregations.asMap().get("proxy") instanceof UnmappedTerms) {
            return new ArrayList<>();
        }

        StringTerms resultTerms = (StringTerms) aggregations.asMap().get("proxy");


        List<StringTerms.Bucket> buckets = resultTerms.getBuckets();

        List<ProxyResultDto> runResultDtos = new ArrayList<>();

        if (CollectionUtils.isEmpty(buckets)) {
            return new ArrayList<>();
        } else {
            buckets.forEach(bucket -> {
                String ip = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();

                logger.info("ip:{}, count:{}", ip, docCount);
                Aggregations subAgg = bucket.getAggregations();
                if (subAgg != null) {
                    Map<String, Aggregation> stringAggregationMap = subAgg.asMap();

                    InternalFilter internalFilter = (InternalFilter) stringAggregationMap.get("successNum");
                    InternalValueCount successNum = (InternalValueCount) internalFilter.getAggregations().asMap().get("successNum");

                    ProxyResultDto runResultDto = new ProxyResultDto();
                    runResultDto.setProxyIp(ip);
                    runResultDto.setAll(docCount);
                    runResultDto.setSuccess(successNum.getValue());
                    runResultDto.setFail(docCount - successNum.getValue());
//                runResultDto.setStartTime(new Date(request.getStartTime()));
//                runResultDto.setEndTime(new Date(request.getEndTime()));
                    runResultDtos.add(runResultDto);
                }
            });
            return runResultDtos;
        }


    }

    /**
     * 统计爬虫模板当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */
    @Override
    public TemplateResultDto countTemplateResult(TaskLogPageRequest request) {

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilderAll = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("creatTime").gte(request.getStartTime()).lte(request.getEndTime()));
        BoolQueryBuilder queryBuilderSuccess = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("creatTime").gte(request.getStartTime()).lte(request.getEndTime()));

        if (request.getTempId() != null) {
            queryBuilderAll.must(QueryBuilders.termQuery("tempId", request.getTempId()));
            queryBuilderSuccess.must(QueryBuilders.termQuery("tempId", request.getTempId()));
        }
        searchQuery.withQuery(queryBuilderAll);
        long allCount = elasticsearchTemplate.count(searchQuery.build(), TaskLog.class);
        queryBuilderSuccess.must(QueryBuilders.matchQuery("status", Constant.TASK_LOG_STATUS.SUCCESS));
        searchQuery.withQuery(queryBuilderSuccess);
        long successCount = elasticsearchTemplate.count(searchQuery.build(), TaskLog.class);

        TemplateResultDto templateResultDto = new TemplateResultDto();
        templateResultDto.setTempId(request.getTempId());
        templateResultDto.setAll(allCount);
        templateResultDto.setFail(allCount - successCount);
        templateResultDto.setSuccess(successCount);
        templateResultDto.setStartTime(DateUtil.date(request.getStartTime()));
        templateResultDto.setEndTime(DateUtil.date(request.getEndTime()));
        if (allCount == 0L) {
            templateResultDto.setSuccessRate(0);
        } else {
            templateResultDto.setSuccessRate(NumberUtil.div(successCount, allCount, 5));
        }

        return templateResultDto;
    }


}
