package com.jin10.spider.spiderserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.mapper.SysUserMapper;
import com.jin10.spider.spiderserver.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.PortResolverImpl;

import java.util.List;

/**
 * @author Airey
 * @date 2019/11/14 14:27
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class SysUserMapperTest {


    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void testfindSysUserList() {

//        List<SysUser> sysUserList = sysUserMapper.findSysUserList();
//        System.out.println(sysUserList);
//
//        IPage<SysUser> sysUserList = userService.findSysUserList(1, 3);
//        System.out.println(sysUserList);

    }




}
