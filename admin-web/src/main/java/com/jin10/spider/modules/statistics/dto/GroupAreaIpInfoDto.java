package com.jin10.spider.modules.statistics.dto;

import com.jin10.spider.modules.task.entity.IpInfo;
import lombok.Data;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-12 16:57
 * ----------------------------------------------
 */
@Data
public class GroupAreaIpInfoDto  {
    private String area;
    private List<IpInfo> ipInfos;
}
