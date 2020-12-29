package com.jin10.spider.spiderserver;

import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.spiderserver.controller.SysUserController;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.service.ISysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Airey
 * @date 2019/10/30 14:57
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysUserController sysUserController;

    @Test
    public void testSet() {

        boolean set = redisUtils.set("air", "air123");
        List<Object> list = Arrays.asList("1", "2", "3");
        boolean list1 = redisUtils.lSet("list", list);
        System.out.println("list1 = " + list1);
        List<Object> list2 = redisUtils.lGet("list", 0, 1);
        System.out.println("list2 = " + list2);

        System.out.println("set= " + set);


    }




}
