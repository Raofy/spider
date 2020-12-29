package com.jin10.spider.spiderserver.controller;

import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.form.SpiderMessageSearchForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: pancg
 * @Date: 2020/7/14 12:27
 */
@SpringBootTest
class SpiderMessageControllerTest {

    @Autowired
    SpiderMessageController spiderMessageController;

    @Test
    void search() {
        SpiderMessageSearchForm searchForm = new SpiderMessageSearchForm();
        searchForm.setTitle("新浪 银行");
        BaseResponse search = spiderMessageController.search(searchForm);

    }
}