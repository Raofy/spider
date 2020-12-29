package com.jin10.spider.modules.template.mapper;

import com.jin10.spider.common.utils.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: pancg
 * @Date: 2020/9/6 23:24
 */

@SpringBootTest
class TemplateMapperTest {

    @Autowired
    TemplateMapper templateMapper;

    @Test
    void updateStatusById() {
        templateMapper.updateStatusById(60L, Constant.TEMPLATE.STATUS_STOP);
    }
}