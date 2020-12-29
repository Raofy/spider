package com.jin10.spider.admin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author hongda.fang
 * @date 2019-12-05 14:48
 * ----------------------------------------------
 */
public class IpValidText {

    public static void main(String[] args) {

        String ip = "122.224.65.201:3128";
//        String url = "http://www.customs.gov.cn/customs/302249/2480148/index.html";
//        String url = "m.baidu.com";
        String url = "https://www.baidu.com/";

        String[] split = ip.split(":");
        if (split.length != 2){
            return;
        }
        InetSocketAddress addr = new InetSocketAddress(split[0], Integer.valueOf(split[1]));
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        HttpResponse execute = HttpRequest.get(url)
                .setProxy(proxy)
                .timeout(5000)
                .execute();
        System.out.println(execute.getStatus());

    }
}
