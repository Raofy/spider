/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 *
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}
