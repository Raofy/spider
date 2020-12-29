package com.jin10.spider.modules.statistics.bean;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-13 14:45
 * ----------------------------------------------
 * 警告基础类
 */
@Data
public class BaseWarnBean {

    /**
     * min 第一次报警间隔
     */
    private int intervalTime1;

    /**
     * min 第二次报警间隔
     */
    private int intervalTime2;


    /**
     * 第三次报警间隔
     */
    private int intervalTime3;

    /**
     * 发布次数
     */
    private int pushTimes = 0;


    private String type;

    private int times;

    private long firstTime = System.currentTimeMillis();

    private long prePushTime = System.currentTimeMillis();


    public BaseWarnBean() {

    }


    public BaseWarnBean(int intervalTime1, String type) {
        this(intervalTime1, intervalTime1 * 6, intervalTime1 * 12, type);
    }

    public BaseWarnBean(int intervalTime1, int intervalTime2, String type) {
        this.intervalTime1 = intervalTime1;
        this.intervalTime2 = intervalTime2;
        this.intervalTime3 = intervalTime2 * 5;
        this.type = type;
    }

    public BaseWarnBean(int intervalTime1, int intervalTime2, int intervalTime3, String type) {
        this.intervalTime1 = intervalTime1;
        this.intervalTime2 = intervalTime2;
        this.intervalTime3 = intervalTime3;
        this.type = type;
    }


    public void addPushTimes() {
        pushTimes += 1;
    }

    public void addTimes() {
        times += 1;
    }

    public boolean canPush() {
        long cur = System.currentTimeMillis();
        if (pushTimes == 0) {
            if (cur - firstTime > intervalTime1 * 60 * 1000) {
                prePushTime = cur;
                pushTimes = pushTimes + 1;
                return true;
            }
        } else if (pushTimes < 10) {
            if (cur - prePushTime > intervalTime2 * 60 * 1000) {
                prePushTime = cur;
                pushTimes = pushTimes + 1;
                return true;
            }
        }
        return false;
    }


    /**
     * 是否可以报警
     *
     * @return
     */
    public boolean whePush() {

        long cur = System.currentTimeMillis();

        if (pushTimes == 0) {
            prePushTime = cur;
            pushTimes = pushTimes + 1;
            return true;
        }

        if (pushTimes == 1) {
            if (cur - prePushTime > intervalTime1 * 60 * 1000) {
                prePushTime = cur;
                pushTimes = pushTimes + 1;
                return true;
            }
        }

        if (pushTimes == 2) {
            if (cur - prePushTime > intervalTime2 * 60 * 1000) {
                prePushTime = cur;
                pushTimes = pushTimes + 1;
                return true;
            }
        }

        if (pushTimes > 2) {
            if (cur - prePushTime > intervalTime3 * 60 * 1000) {
                prePushTime = cur;
                pushTimes = pushTimes + 1;
                return true;
            }
        }

        return false;
    }


}
