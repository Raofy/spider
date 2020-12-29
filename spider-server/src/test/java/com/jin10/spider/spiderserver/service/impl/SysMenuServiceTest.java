package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.SysMenu;
import com.jin10.spider.spiderserver.service.ISysMenuService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Airey
 * @date 2019/11/20 10:42
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysMenuServiceTest {


    @Autowired
    private ISysMenuService menuService;

    @Test
    public void SysMenuTest(){

        List<SysMenu> sysMenuList = menuService.findSysMenuList();
        System.out.println(sysMenuList);

    }

}
