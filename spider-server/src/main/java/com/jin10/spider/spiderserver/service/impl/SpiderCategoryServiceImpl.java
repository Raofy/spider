package com.jin10.spider.spiderserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin10.spider.spiderserver.entity.SpiderCategory;
import com.jin10.spider.spiderserver.mapper.SpiderCategoryMapper;
import com.jin10.spider.spiderserver.service.ISpiderCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息类别管理 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-29
 */
@Service
public class SpiderCategoryServiceImpl extends ServiceImpl<SpiderCategoryMapper, SpiderCategory> implements ISpiderCategoryService {


    @Override
    public List<SpiderCategory> frontList() {
        QueryWrapper<SpiderCategory> queryWrapper=new QueryWrapper<>();
        queryWrapper.select(SpiderCategory.class,info->!info.getColumn().equals("category_color"));
        List<SpiderCategory> categoryList = baseMapper.selectList(queryWrapper);
        return categoryList;
    }

    /**
     * 根据category查询是否存在
     *
     * @param category
     * @return
     */
    @Override
    public SpiderCategory selectOne(String category) {

        LambdaQueryWrapper<SpiderCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpiderCategory::getCategoryName,category);

        SpiderCategory spiderCategory = baseMapper.selectOne(queryWrapper);

        return spiderCategory;
    }
}
