package com.jin10.spider;

/**
 * @author hongda.fang
 * @date 2019-11-28 17:52
 * ----------------------------------------------
 */
public class MyTest {


    public static void main(String[] args) {
        String proxy = "192.168.88.175:8908";
        String[] split = proxy.split(":");
        System.out.println(proxy);
        System.out.println(split[0]);
        System.out.println(split[1]);
    }

}
