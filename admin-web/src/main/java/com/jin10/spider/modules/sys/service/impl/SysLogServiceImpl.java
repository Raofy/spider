/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.utils.Query;
import com.jin10.spider.modules.sys.dao.SysLogDao;
import com.jin10.spider.modules.sys.entity.SysLogEntity;
import com.jin10.spider.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");

        IPage<SysLogEntity> page = this.page(
            new Query<SysLogEntity>().getPage(params),
            new QueryWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key),"username", key)
        );

        return new PageUtils(page);
    }
}
