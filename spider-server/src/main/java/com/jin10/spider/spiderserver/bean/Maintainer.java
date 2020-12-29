package com.jin10.spider.spiderserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Airey
 * @date 2020/3/27 17:37
 * ----------------------------------------------
 * 维护人员优先级
 * ----------------------------------------------
 */
@Data
@AllArgsConstructor
public class Maintainer implements Comparable<Maintainer> {


    private String username;

    private Long systemTime;


    @Override
    public int compareTo(Maintainer o) {
        return this.systemTime > o.getSystemTime() ? 1 : -1;
    }
}
