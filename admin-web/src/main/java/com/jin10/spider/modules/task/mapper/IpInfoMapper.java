package com.jin10.spider.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jin10.spider.modules.task.entity.IpInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理ip 信息 Mapper 接口
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-31
 */
@Mapper
public interface IpInfoMapper extends BaseMapper<IpInfo> {

    @Select("SELECT * FROM ip_info GROUP BY ip,id,check_time,create_time,expire_time,expire_time,is_foreign,delay,check_times,area,platform,type,extra,deleted,is_valid,proxy_level")
    List<IpInfo> groupByIp();

    /**
     * 查询代理有效数量
     *
     * @param proxyLevel
     * @return
     */
    Long selectCount(int proxyLevel);

}