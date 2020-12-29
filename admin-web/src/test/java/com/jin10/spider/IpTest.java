package com.jin10.spider;

import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.service.IIpInfoService;
import com.jin10.spider.modules.task.handler.CheckIpHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author hongda.fang
 * @date 2019-10-31 14:45
 * ----------------------------------------------
 */
@SpringBootTest
public class IpTest {

    @Autowired
    private IIpInfoService ipInfoService;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        String s = "117.69.201.253,49.89.85.14,59.57.38.232,114.239.252.141,121.226.215.4,49.70.89.56,117.95.162.212,175.43.59.26,183.164.239.31,117.57.91.147,182.34.32.59,183.166.86.149,183.164.238.252,123.163.96.170,1.198.72.222,1.197.204.177,59.57.148.85,1.197.204.49,114.239.198.215,1.197.203.249,27.152.91.77,222.190.222.246,114.239.151.101,117.57.90.193,49.70.85.9,182.34.35.8,183.166.70.255,115.210.67.169,117.28.96.93,114.239.110.190,27.43.186.186,114.239.172.23,114.239.250.14,117.57.90.248,183.164.239.71,183.166.111.39,27.43.189.219,117.30.112.202,27.152.91.245,222.89.32.132,183.164.239.84,183.166.124.129,182.34.33.183,171.12.113.150,121.205.84.80,182.34.37.4,123.163.97.154,110.243.11.39,59.57.149.218,117.57.91.182,59.57.148.50,59.57.148.4,58.253.156.58,121.205.14.10,117.30.113.130,120.83.109.165,114.239.150.235,182.35.87.114,123.52.97.225,60.13.42.176,117.57.91.205,114.239.42.216,113.124.94.72,114.239.172.20,117.69.200.70,117.57.90.245,117.69.200.223,27.152.90.96,183.164.238.68,144.123.69.216,114.239.249.51,27.152.90.155,125.78.176.185,117.28.97.222,49.89.222.253,117.57.90.202,117.57.90.253,117.69.200.29,121.232.111.96,171.35.170.138,117.57.90.73,117.57.90.189,113.194.31.62,182.35.87.89,183.164.238.137,120.83.111.18,117.95.199.139,117.57.91.240,117.57.90.150,117.69.201.137,121.226.188.41,117.30.113.176,58.253.152.244,183.164.238.179,183.164.239.34,183.166.7.2,117.95.201.104,121.226.188.158,117.57.91.54,114.239.255.105";
		String[] split = s.split(",");
		List<String> strings = Arrays.asList(split);

		List<IpInfo> ipInfos = new ArrayList<>();
        for (String ip : strings){
            IpInfo ipInfo = new IpInfo();
            ipInfo.setIp(ip + ":9999");
            ipInfos.add(ipInfo);
        }
        CheckIpHandler checkIpHandler = new CheckIpHandler();
        List<IpInfo> unValidIp = checkIpHandler.checkIp(ipInfos, 10000);
        String a  = "";
    }

    @Test
    public void testIp(){
        String a = "";
    }


}
