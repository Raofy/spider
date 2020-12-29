package com.jin10.spider.modules.task.bean;

import com.jin10.spider.modules.task.entity.IpInfo;
import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-06 11:12
 * ----------------------------------------------
 * <p>
 * ip 优先级
 */
@Data
public class PriorityIp implements Comparable<PriorityIp> {

    private String ip;
    private long addTime;
    /**
     * 1 随机代理 ｜ 2 低质量代理 ｜ 3 高质量代理
     */
    private int proxyLevel;

    private String area;

    public PriorityIp() {

    }


    public PriorityIp(IpInfo info) {
        this.ip = info.getIp();
        this.addTime = System.currentTimeMillis();
        this.proxyLevel = info.getProxyLevel();
        this.area = info.getArea();
    }

    @Override
    public int compareTo(PriorityIp o) {
        if (o.addTime < this.addTime) {
            return 1;
        } else {
            if (o.addTime == this.addTime) {
                return o.proxyLevel < this.proxyLevel ? 1 : -1;
            } else {
                return -1;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PriorityIp){
            PriorityIp priorityIp = (PriorityIp) obj;
            return priorityIp.getIp().equals(this.getIp());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getIp().hashCode();
    }
}
