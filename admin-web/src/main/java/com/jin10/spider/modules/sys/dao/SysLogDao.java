/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.modules.sys.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jin10.spider.modules.sys.entity.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志
 *
 *
 */
@Mapper
public interface SysLogDao extends BaseMapper<SysLogEntity> {
	
}
