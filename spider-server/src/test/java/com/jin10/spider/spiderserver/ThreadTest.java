package com.jin10.spider.spiderserver;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;

/**
 * @author Airey
 * @date 2020/6/15 17:14
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
public class ThreadTest {

    public static void main(String[] args) {


        String url = "http://115.29.174.245:7200/partyPushMsg";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9173581245409163566%22%7D&n_type=0&p_from=1");

        jsonObject.put("source", "新浪微博");
        jsonObject.put("category", "微博");
        jsonObject.put("time", 1592212719000L);

        for (int i = 0; i < 100; i++) {
            jsonObject.put("title", "测试消息111: 美足协取消运动员奏国歌时必须站立的规定，特朗普怒发推特 == " + i);
            HttpResponse execute = HttpRequest.get(url).form(jsonObject).execute();
            System.out.println(execute.body());
        }



    }

}
