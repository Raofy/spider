package com.jin10.spider.spiderserver;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Airey
 * @date 2019/11/19 10:55
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class MsgIdTest {

    @Test
    public void generateMsgId() {

        String s = UUID.randomUUID().toString();
        System.out.println(s);
        String s1 = java.util.UUID.randomUUID().toString();
        System.out.println(s1);
        int i = UUID.randomUUID().hashCode();
        System.out.println(i);

    }

}
