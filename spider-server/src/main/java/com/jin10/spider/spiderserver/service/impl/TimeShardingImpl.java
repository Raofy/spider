package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;
import java.util.Date;

/**
 * @author Airey
 * @date 2020/2/25 17:37
 * ----------------------------------------------
 *  根据时间分表的分片算法
 *  逻辑表名:spider_message
 *  真实表名:spider_message_2019_1  类似
 * ----------------------------------------------
 */
@Slf4j
public class TimeShardingImpl implements PreciseShardingAlgorithm<Date> {

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> shardingValue) {


        StringBuffer tableName = new StringBuffer();
        Date date = shardingValue.getValue();

        int year = DateUtil.year(date);
        int month = DateUtil.month(date) + 1;
        int key;

        if (month <= 6) {
            key = 0;
        } else {
            key = 1;
        }

        tableName.append(shardingValue.getLogicTableName())
                .append("_")
                .append(year)
                .append("_")
                .append(key);
        log.info("插入的分表的名称为 ==> {}", tableName.toString());
        return tableName.toString();
    }


}
