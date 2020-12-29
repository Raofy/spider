package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.mapper.SysMenuMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Airey
 * @date 2019/12/12 10:43
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysMenuMappperTest {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Test
    public void findSysMenuByUserId() {
        Long userId = 1L;
        sysMenuMapper.findSysMenuByUserId(userId);
    }

}
