package com.jin10.spider.modules.statistics.mapper;

import com.jin10.spider.modules.statistics.entity.SpiderFail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 爬虫失败信息 Mapper 接口
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-18
 */
@Mapper
public interface SpiderFailMapper extends BaseMapper<SpiderFail> {

}
