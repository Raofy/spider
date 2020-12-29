package com.jin10.spider.modules.statistics.service.impl;

import com.jin10.spider.modules.statistics.entity.SpiderFail;
import com.jin10.spider.modules.statistics.mapper.SpiderFailMapper;
import com.jin10.spider.modules.statistics.service.ISpiderFailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 爬虫失败信息 服务实现类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-18
 */
@Service
public class SpiderFailServiceImpl extends ServiceImpl<SpiderFailMapper, SpiderFail> implements ISpiderFailService {

}
