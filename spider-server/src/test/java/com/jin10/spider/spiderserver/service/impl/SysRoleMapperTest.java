package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.SysRole;
import com.jin10.spider.spiderserver.mapper.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Airey
 * @date 2019/11/14 16:51
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class SysRoleMapperTest {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void testfindSysRoleList(){

        List<SysRole> sysRoleList = sysRoleMapper.findSysRoleList();
        System.out.println(sysRoleList);

    }


}
