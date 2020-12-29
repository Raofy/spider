package com.jin10.spider.modules.task.controller;

import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.statistics.receiver.TaskLogMqReceiver;
import com.jin10.spider.modules.task.service.IProductTaskService;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.service.ITemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Airey
 * @date 2020/1/14 17:47
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@RestController
@RequestMapping("redis")
@Slf4j
public class RedisController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IProductTaskService productTaskService;


    @Autowired
    private ThreadPoolTaskExecutor asyncMq;


    @Autowired
    private TaskLogMqReceiver taskLogMqReceiver;

    private Integer count = 0;


    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("set")
    public BaseResponse redisSet() {

        List<Template> list = templateService.list();

        List<Template> all = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            all.add(list.get(0));
        }

        boolean airey = redisUtils.lSet("template", all);

        return BaseResponse.ok(airey);
    }

    @GetMapping("get")
    public BaseResponse redisGet() {

        long size = redisUtils.lGetListSize("template");

        Object template = redisUtils.lGetIndex("template", 1);

        List<Object> template1 = redisUtils.lGet("template", 0, size);

        List<Template> templates = (List<Template>) template1.get(0);

        return BaseResponse.ok(templates);
    }

    @GetMapping("testCreate")
    public BaseResponse testCreate() {

        productTaskService.startProductTask();


        return BaseResponse.ok();
    }


    @PostMapping("getValue")
    public BaseResponse getValue(@RequestParam("key") String key) {

        Object o = redisUtils.get(key);

        return BaseResponse.ok(o);

    }


    @GetMapping("setHashValue")
    public BaseResponse setHashValue() {


        redisUtils.hset("urlmap", "url321", "321");

        redisUtils.hset("urlmap", "url456", "456");

        Object urlv1 = redisUtils.hget("urlmap", "url321");
        Object urlv2 = redisUtils.hget("urlmap", "url456");
        Object urlv3 = redisUtils.hget("urlmap", "url789");

        log.info("urlv1 : value = " + urlv1);
        log.info("urlv2 : value = " + urlv2);
        log.info("urlv3 : value = " + urlv3);

        Map<String, Object> keymap = new HashMap<>();

        keymap.put("111", "111");
        keymap.put("222", "222");
        keymap.put("333", "333");


        redisUtils.hmset("keymap", keymap);

        Map<Object, Object> result = redisUtils.hmget("keymap");
        log.info("result : " + result);

        HashMap<String, Object> keymap2 = new HashMap<>();
        keymap2.put("123432", "2123242");

        redisUtils.hmset("keymap", keymap2);
        Map<Object, Object> result2 = redisUtils.hmget("keymap");
        log.info("result2 : " + result2);


        return BaseResponse.ok();
    }


    @GetMapping("del")
    public BaseResponse delete() {

        int count = 0;

        for (int i = 800; i < 2027; i++) {
            if (redisUtils.hasKey(String.valueOf(i))) {
                redisUtils.del(String.valueOf(i));
                count++;
            }

        }
        return BaseResponse.ok(count);
    }


    @GetMapping("expire")
    public BaseResponse expire() {

        boolean set = redisUtils.set("Airey", "Airey", 10);


        return BaseResponse.ok(set);
    }

    @GetMapping("screen")
    public BaseResponse screen() {

        Set<String> keys = redisUtils.keys(RedisKey.TEMPLATE_SUFFIX);
        List<Template> list = new ArrayList<>();
        keys.forEach(item -> {
            Object o = redisUtils.get(item);
            if (o instanceof Template) {
                Template template = (Template) o;
                if (template.getPushNum() != 5) {
                    list.add(template);
                }
            }
        });

        return BaseResponse.ok(list);

    }


    @GetMapping("/sync")
    public BaseResponse sync() {

        atomicInteger = new AtomicInteger(0);

//        taskLogMqReceiver.process("i");

        for (int i = 0; i < 5000; i++) {
            taskLogMqReceiver.process("i" + i);
        }

        log.info("atomicInteger  = " + atomicInteger.get());

        return BaseResponse.ok(atomicInteger.get());
    }


    private void add() {
//        count++;
//        log.info("count = " + count);
        atomicInteger.incrementAndGet();
    }


    @GetMapping("list")
    public BaseResponse redisList() {


        String key = "airList";

        redisUtils.lSet(key, 123);
        redisUtils.lSet(key, 456);
        redisUtils.lSet(key, 789);


        System.out.println("redis key : " + redisUtils.lGet(key, 0, -1));


        String key2 = "greeList";

        List<String> list = Arrays.asList("adf", "w2d", "fgre");

        redisUtils.lSetAll(key2, Collections.singletonList(list));

        System.out.println("redis key2 : " + redisUtils.lGet(key2, 0, -1));


        String key3 = "airSet";

        redisUtils.sSet(key3, 123);
        redisUtils.sSet(key3, 123);
        redisUtils.sSet(key3, 234, 788, 234);

        List<Integer> integers = Arrays.asList(123, 456, 789);

        Integer[] integers1 = integers.toArray(new Integer[0]);


        redisUtils.sSet(key3, integers1);

        Set<Object> objects = redisUtils.sGet(key3);

        System.out.println("redis key3 : " + objects);


        return BaseResponse.ok();
    }


    @GetMapping("zset")
    public BaseResponse zset() {


        String key = "Zset:111";

        redisUtils.zAdd(key, "127.0.0.1", System.currentTimeMillis());


        redisUtils.zAdd(key, "192.168.13.235", System.currentTimeMillis());


        redisUtils.zAdd(key, "192.168.13.168", System.currentTimeMillis());


        Set<Object> objects = redisUtils.zRange(key, 0, -1);

        System.out.println("objects: " + objects);

        Set<Object> reverseRange1 = redisUtils.zRange(key, 0, 0);
        List<Object> list = new ArrayList<>(reverseRange1);
        String next = (String) reverseRange1.iterator().next();
        if (list.size() == 1) {
            String ip = (String) list.get(0);

            System.out.println("ip : " + ip);
            System.out.println("next : " + next);
        }
        redisUtils.zAdd(key, next, System.currentTimeMillis());

        Set<Object> objects1 = redisUtils.zRange(key, 0, -1);

        System.out.println("objects1: " + objects1);


        Set<Object> reverseRange = redisUtils.zReverseRange(key, 0, -1);

        System.out.println("reverseRange : " + reverseRange);


        return BaseResponse.ok();
    }


    @GetMapping("/integer")
    public void inter(){
        Integer number=3;
        redisUtils.hset("numberMap","numberI",number);
        System.out.println(redisUtils.hget("numberMap","numberI"));
        redisUtils.hset("numberMap","numberI",++number);
        System.out.println(redisUtils.hget("numberMap","numberI"));

        redisUtils.hincr("numberMap","numberI",1);
        System.out.println(redisUtils.hget("numberMap","numberI"));

    }


    @GetMapping("hash")
    public BaseResponse hash(){

        redisUtils.hset("blockMap","admin",799);
        System.out.println(redisUtils.hmget("blockMap"));

        redisUtils.hset("blockMap","admin",987);
        System.out.println(redisUtils.hmget("blockMap"));


        return BaseResponse.ok();
    }


}
