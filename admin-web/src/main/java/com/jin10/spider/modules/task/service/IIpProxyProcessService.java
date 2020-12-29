package com.jin10.spider.modules.task.service;

import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.modules.statistics.dto.GroupAreaIpInfoDto;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.entity.IpInfo;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-03 14:57
 * ----------------------------------------------
 * <p>
 * ip代理
 */
public interface IIpProxyProcessService {



    /**
     * 检验IP
     *
     * @param proxyLevel 代理等级
     * @param foreign 是否国外  true 国外 false 国内
     */
    void checkIp(int proxyLevel,boolean foreign);

    /**
     * 寻找合适的ip
     *
     * @param urlTaskDtos
     * @return
     */
    void takeProxyIp(List<UrlTaskDto> urlTaskDtos);

    /**
     * 更新单个ip队列
     * @param ipInfo
     */
    void updateIpQueue(IpInfo ipInfo);

    /**
     * 按地区分组
     *
     * @return
     */
    List<GroupAreaIpInfoDto> groupAreaIpInfo(List<IpInfo> vaildIpInfoList);

}
