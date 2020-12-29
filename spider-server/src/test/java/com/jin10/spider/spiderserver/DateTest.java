package com.jin10.spider.spiderserver;

import cn.hutool.core.date.DateUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author Airey
 * @date 2019/11/12 11:38
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class DateTest {


    @Test
    public void test(){
        String now = DateUtil.now();
        System.out.println(now);

        String today = DateUtil.today();
        System.out.println(today);

        int hour = DateUtil.hour(new Date(), true);
        System.out.println(hour);

    }


}
