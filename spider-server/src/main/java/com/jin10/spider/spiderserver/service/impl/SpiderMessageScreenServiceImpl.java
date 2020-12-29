package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.SpiderMessageScreen;
import com.jin10.spider.spiderserver.mapper.SpiderMessageScreenMapper;
import com.jin10.spider.spiderserver.service.ISpiderMessageScreenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 筛选爬虫消息列表 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-12-09
 */
@Service
public class SpiderMessageScreenServiceImpl extends ServiceImpl<SpiderMessageScreenMapper, SpiderMessageScreen> implements ISpiderMessageScreenService {

    @Override
    public void deleteAll(Long userId) {
        baseMapper.deleteAll(userId);
    }
}
