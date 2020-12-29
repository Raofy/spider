package com.jin10.spider.admin;

import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.service.IIpInfoService;
import com.jin10.spider.modules.task.service.impl.IpInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * @author Airey
 * @date 2020/1/7 18:25
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class ipInfoTest {

    @Autowired
    private IIpInfoService ipInfoService;

    @Test
    public void test(){
        Map<String, IpInfo> allMap = ipInfoService.findAllMap();
        System.out.println(allMap);
    }

}
