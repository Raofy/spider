package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.Template;
import com.jin10.spider.spiderserver.mapper.TemplateMapper;
import com.jin10.spider.spiderserver.service.ITemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 爬虫模版 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2020-06-03
 */
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {

    /**
     * 根据id查询网址
     *
     * @param tempId
     * @return
     */
    @Override
    public String findSiteByTempId(Long tempId) {

        Optional<Template> optional = Optional.ofNullable(baseMapper.selectById(tempId));
        if (optional.isPresent()) {
            Template template = optional.get();
            return template.getPageSite();
        }

        return null;
    }
}
