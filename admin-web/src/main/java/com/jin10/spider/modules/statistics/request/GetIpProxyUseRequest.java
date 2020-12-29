package com.jin10.spider.modules.statistics.request;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-12 18:39
 * ----------------------------------------------
 */
@Data
public class GetIpProxyUseRequest  {


    private Integer action;

    private String proxyIp;

    private boolean subscribe = true;

    @Override
    public int hashCode() {
        return (proxyIp + getAction()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GetIpProxyUseRequest){
            GetIpProxyUseRequest request = (GetIpProxyUseRequest) o;
            return (request.getProxyIp() + request.getAction()).equalsIgnoreCase(proxyIp + getAction());
        }
        return super.equals(o);
    }
}
