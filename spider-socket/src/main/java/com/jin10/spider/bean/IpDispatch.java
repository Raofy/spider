package com.jin10.spider.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

/**
 * @author Airey
 * @date 2020/3/31 14:28
 * ----------------------------------------------
 * 优先Ip队列
 * ----------------------------------------------
 */
@Data
@AllArgsConstructor
public class IpDispatch implements Comparable<IpDispatch> {

    /**
     * 爬虫机ip
     */
    private String ip;

    /**
     * 执行时间
     */
    private Long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IpDispatch)) {
            return false;
        }
        IpDispatch that = (IpDispatch) o;
        return getIp().equals(that.getIp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIp());
    }

    @Override
    public int compareTo(IpDispatch o) {
        return this.time > o.getTime() ? 1 : -1;
    }


}
