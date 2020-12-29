package com.jin10.spider.spiderserver.controller;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.constants.GlobalConstants;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.spiderserver.entity.SpiderMessage;
import com.jin10.spider.spiderserver.entity.SpiderMessageElastics;
import com.jin10.spider.spiderserver.entity.SysGroupMonitor;
import com.jin10.spider.spiderserver.form.SpiderMessageSearchForm;
import com.jin10.spider.spiderserver.message.MqReceiver;
import com.jin10.spider.spiderserver.service.ISpiderMessageService;
import com.jin10.spider.spiderserver.service.ISpiderMsgElasticSearchService;
import com.jin10.spider.spiderserver.service.ISysGroupMonitorService;
import com.jin10.spider.spiderserver.utils.PagewithLabelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static com.jin10.spider.spiderserver.constants.GlobalConstants.CACHE_QUEUE_SIZE;

/**
 * <p>
 * 爬虫消息列表 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-18
 */
@RestController
@RequestMapping("/spider-message")
@Slf4j
public class SpiderMessageController {

    @Autowired
    private ISpiderMessageService messageService;

    @Autowired
    private ISpiderMsgElasticSearchService searchService;

    @Autowired
    private MqReceiver mqReceiver;

    @Autowired
    private ISysGroupMonitorService sysGroupMonitorService;

    /**
     * 获取最新的指定消息数量
     *
     * 首次打开后台终端页面的时候进行请求的接口
     *
     * @return
     */
    @GetMapping("latestList")
    public BaseResponse findLatestMessageList() {
        log.info("开始获取最新 { " + CACHE_QUEUE_SIZE + " } 条爬虫数据");
        return BaseResponse.ok(new MsgResponse(MsgCodeEnum.LATEST, messageService.findLatestMessageList()));
    }

    /**
     * 根据标题查询es里面的数据
     *
     * @param title
     * @return
     */
    @GetMapping("search123")
    public BaseResponse searchByTitle(String title) {

        log.info("开始从ElasticSearch 中搜索 title 为 { " + title + " } 的记录！");
        // List<SpiderMessageElastics> spiderMessageElastics = searchService.findByTitle(title);
        List<SpiderMessageElastics> list = matchQueryList(title);
        return BaseResponse.ok(list);
    }

    @GetMapping("findCompleteMessageList")
    public BaseResponse findCompleteMessageList(){
        List<SpiderMessage> completeMessageList = messageService.findCompleteMessageList(1591755647000L);
        return BaseResponse.ok(completeMessageList);
    }

    @GetMapping("findById")
    public BaseResponse findById(String msgId) {
//       SpiderMessageElastics byMsgId = searchService.findByMsgId(msgId);
        List<String> list = Arrays.asList(msgId);
        List<SpiderMessageElastics> byMsgIdIn = searchService.findByMsgIdIn(list);
        return BaseResponse.ok(byMsgIdIn);
    }

    /**
     * 根据传入的条件查询对应的列表
     *
     * @param searchForm
     * @return
     */
    @PostMapping("search")
    public BaseResponse search(@RequestBody SpiderMessageSearchForm searchForm) {

        log.info("开始从ElasticSearch搜索实时数据, 搜索条件为 " + searchForm);

        Integer pageNum;

        Integer pageSize;

        // 页码 ES的分页是从第0页开始的
        if (searchForm.getPageNum() == null || searchForm.getPageNum() < 0) {
            pageNum = 0;
        } else {
            pageNum = searchForm.getPageNum() - 1;
        }
        // 页数
        if (searchForm.getPageSize() == null || searchForm.getPageSize() < 1) {
            pageSize = 10;
        } else {
            pageSize = searchForm.getPageSize();
        }

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder query = QueryBuilders.boolQuery();


        Map<String, Object> labelSmap = new ConcurrentHashMap<>(DataCache.labelMap);

        if (StringUtils.isNotBlank(searchForm.getTitle())) {
            String title = searchForm.getTitle();
            // 根据一个或者多个 空格分钟
            String [] arr = title.split("\\s+");
            for(String str : arr){
                query.should(QueryBuilders.matchPhraseQuery("title", str));
                labelSmap.put(str, GlobalConstants.SEARCH_COLOR);
            }
        }
        if (StringUtils.isNotBlank(searchForm.getPageSite())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("url", searchForm.getPageSite()));
        }
        if (StringUtils.isNotBlank(searchForm.getCategory())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("category", searchForm.getCategory()));
        }
        if (StringUtils.isNotBlank(searchForm.getChannel())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("channel", searchForm.getChannel()));
        }
        if (StringUtils.isNotBlank(searchForm.getSource())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("source", searchForm.getSource()));
        }
        if (StringUtils.isNotBlank(searchForm.getTaskId())) {
            queryBuilder.must(QueryBuilders.matchQuery("taskId", searchForm.getTaskId()));
        }
        if (ObjectUtil.isNotNull(searchForm.getTempId())) {
            queryBuilder.must(QueryBuilders.matchQuery("tempId", searchForm.getTempId()));
        }
        if (ObjectUtil.isNotNull(searchForm.getStartTime())) {
            queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte(searchForm.getStartTime().getTime()));
        }
        if (ObjectUtil.isNotNull(searchForm.getEndTime())) {
            queryBuilder.must(QueryBuilders.rangeQuery("insertTime").lte(searchForm.getEndTime().getTime()));
        }
        //搜索结果是否展示隐藏的源
        if(!searchForm.isWhetherToShowOrHide()){
            List<SysGroupMonitor> sysGroupMonitors = sysGroupMonitorService.filterList();
            if(sysGroupMonitors != null && !sysGroupMonitors.isEmpty()){
                BoolQueryBuilder query2;
                for(SysGroupMonitor sysGroupMonitor : sysGroupMonitors){
                    query2 = QueryBuilders.boolQuery();
                    query2.must(QueryBuilders.matchPhraseQuery("source",sysGroupMonitor.getSource()));
                    query2.must(QueryBuilders.matchPhraseQuery("channel",sysGroupMonitor.getChannel()));
                    query2.must(QueryBuilders.matchPhraseQuery("category",sysGroupMonitor.getCategory()));
                    queryBuilder.mustNot(query2);
                }
            }
        }


        queryBuilder.must(query);

        searchQuery.withQuery(queryBuilder);

        searchQuery.withPageable(PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("time"))));

        Page<SpiderMessageElastics> search = searchService.search(searchQuery.build());

        search.getContent().forEach(x -> {
            if (DataCache.categoryMap.containsKey(x.getCategory())) {
                x.setCategoryColor((String) DataCache.categoryMap.get(x.getCategory()));
            }
        });

        return BaseResponse.ok(new PagewithLabelUtils(search, labelSmap));
    }


    public List<SpiderMessageElastics> matchQueryList(String key) {

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.matchQuery("title", key));
        Page<SpiderMessageElastics> spiderMessageElastics = searchService.search(builder.build());
        return spiderMessageElastics.getContent();

    }


}
