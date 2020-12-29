package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.utils.Constants;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.utils.QueryEsPage;
import com.jin10.spider.spiderserver.entity.TerminalRecordElastics;
import com.jin10.spider.spiderserver.form.TerminalRecordForm;
import com.jin10.spider.spiderserver.service.ITerminalRecordElasticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;


/**
 * @author Airey
 * @date 2019/12/12 11:30
 * ----------------------------------------------
 * 终端消息上下线记录
 * ----------------------------------------------
 */
@RestController
@RequestMapping("terminal")
@Slf4j
public class TerminalRecordController {

    @Autowired
    private ITerminalRecordElasticsService elasticsService;


    /**
     * 查询终端消息上下线记录列表
     *
     * @param terminalRecordForm
     * @return
     */
    @PostMapping("findHisRecord")
    public BaseResponse findList(@RequestBody TerminalRecordForm terminalRecordForm) {

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        terminalRecordForm.setOrderField("offlineTime");
        terminalRecordForm.setOrder(Constants.DESC);

        if (StringUtils.isNotBlank(terminalRecordForm.getUserName())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("userName", terminalRecordForm.getUserName()));
        }
        if (StringUtils.isNotBlank(terminalRecordForm.getMachineCode())) {
            queryBuilder.must(QueryBuilders.termQuery("machineCode", terminalRecordForm.getMachineCode()));
        }
        if (StringUtils.isNotBlank(terminalRecordForm.getTitle())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("title", terminalRecordForm.getTitle()));
        }

        searchQuery.withQuery(queryBuilder);
        searchQuery.withPageable(QueryEsPage.getEsPage(terminalRecordForm));

        Page<TerminalRecordElastics> search = elasticsService.search(searchQuery.build());
        return BaseResponse.ok(new PageUtils(search));
    }


}
