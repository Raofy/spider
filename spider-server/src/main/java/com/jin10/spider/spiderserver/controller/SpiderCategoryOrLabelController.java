package com.jin10.spider.spiderserver.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.utils.QueryPage;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.constants.SpiderKeyWord;
import com.jin10.spider.spiderserver.entity.SpiderCategory;
import com.jin10.spider.spiderserver.entity.SpiderCategoryOrLabel;
import com.jin10.spider.spiderserver.entity.SpiderLabel;
import com.jin10.spider.spiderserver.service.ISpiderCategoryService;
import com.jin10.spider.spiderserver.service.ISpiderLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Airey
 * @date 2020/3/2 9:12
 * ----------------------------------------------
 * 查询关键字的类别
 * ----------------------------------------------
 */
@RestController
@RequestMapping("spider-type")
@Slf4j
public class SpiderCategoryOrLabelController {

    @Autowired
    private ISpiderCategoryService categoryService;

    @Autowired
    private ISpiderLabelService labelService;


    /**
     * 查询类别列表
     *
     * @return
     */
    @PostMapping("list")
    public BaseResponse list(@RequestBody SpiderCategoryOrLabel categoryOrLabel) {

        Integer type = categoryOrLabel.getType();
        if (ObjectUtil.isNull(type)) {
            log.error("参数校验异常！！！传入的type不能为NULL");
            throw new BaseException("参数校验异常！！！传入的type不能为NULL！");
        }

        if (type.equals(SpiderKeyWord.LABEL)) {
            IPage<SpiderLabel> page = labelService.page(new QueryPage<SpiderLabel>().getPage(categoryOrLabel));
            return BaseResponse.ok(new PageUtils(page));
        }

        if (type.equals(SpiderKeyWord.CATEGORY)) {
            IPage<SpiderCategory> page = categoryService.page(new QueryPage<SpiderCategory>().getPage(categoryOrLabel));
            return BaseResponse.ok(new PageUtils(page));
        }

        return BaseResponse.ok();

    }


    @PostMapping("add")
    public BaseResponse add(@RequestBody SpiderCategoryOrLabel categoryOrLabel) {

        Integer type = categoryOrLabel.getType();
        if (ObjectUtil.isNull(type)) {
            log.error("参数校验异常！！！传入的type不能为NULL");
            throw new BaseException("参数校验异常！！！传入的type不能为NULL！");
        }


        boolean save = false;

        if (type.equals(SpiderKeyWord.LABEL)) {
            SpiderLabel label = new SpiderLabel();
            label.setLabelName(categoryOrLabel.getName());
            label.setLabelColor(categoryOrLabel.getColor());
            save = labelService.save(label);
            DataCache.labelMap.put(label.getLabelName(), label.getLabelColor());
            return BaseResponse.ok(save);
        }

        if (type.equals(SpiderKeyWord.CATEGORY)) {
            SpiderCategory category = new SpiderCategory();
            category.setCategoryName(categoryOrLabel.getName());
            category.setCategoryColor(categoryOrLabel.getColor());
            save = categoryService.save(category);
            DataCache.categoryMap.put(category.getCategoryName(), category.getCategoryColor());
        }

        return BaseResponse.ok(save);

    }


    /**
     * 更新爬虫标签
     *
     * @return
     */
    @PostMapping("update")
    public BaseResponse update(@RequestBody SpiderCategoryOrLabel categoryOrLabel) {

        Integer type = categoryOrLabel.getType();
        if (ObjectUtil.isNull(type)) {
            log.error("参数校验异常！！！传入的type不能为NULL");
            throw new BaseException("参数校验异常！！！传入的type不能为NULL！");
        }

        boolean update = false;


        if (type.equals(SpiderKeyWord.LABEL)) {
            SpiderLabel label = new SpiderLabel();
            label.setLabelName(categoryOrLabel.getName());
            label.setLabelColor(categoryOrLabel.getColor());
            label.setId(categoryOrLabel.getId());
            SpiderLabel record = labelService.getById(label.getId());
            if (ObjectUtil.isNotNull(record)){
                DataCache.labelMap.remove(record.getLabelName());
            }
            update = labelService.updateById(label);
            DataCache.labelMap.put(label.getLabelName(), label.getLabelColor());
        }

        if (type.equals(SpiderKeyWord.CATEGORY)) {
            SpiderCategory category = new SpiderCategory();
            category.setCategoryName(categoryOrLabel.getName());
            category.setCategoryColor(categoryOrLabel.getColor());
            category.setCateId(categoryOrLabel.getId());
            SpiderCategory record = categoryService.getById(category.getCateId());
            if (ObjectUtil.isNotNull(record)){
                DataCache.categoryMap.remove(record.getCategoryName());
            }
            update = categoryService.updateById(category);
            DataCache.categoryMap.put(category.getCategoryName(), category.getCategoryColor());
        }


        return BaseResponse.ok(update);
    }

    @PostMapping("delete")
    public BaseResponse delete(@RequestBody SpiderCategoryOrLabel categoryOrLabel) {

        Integer type = categoryOrLabel.getType();
        if (ObjectUtil.isNull(type)) {
            log.error("参数校验异常！！！传入的type不能为NULL");
            throw new BaseException("参数校验异常！！！传入的type不能为NULL！");
        }

        boolean delete = false;

        if (type.equals(SpiderKeyWord.LABEL)) {
            SpiderLabel label = labelService.getById(categoryOrLabel.getId());
            if (ObjectUtil.isNotNull(label)){
                DataCache.labelMap.remove(label.getLabelName());
            }
            delete = labelService.removeById(categoryOrLabel.getId());
        }

        if (type.equals(SpiderKeyWord.CATEGORY)) {
            SpiderCategory record = categoryService.getById(categoryOrLabel.getId());
            if (ObjectUtil.isNotNull(record)){
                DataCache.categoryMap.remove(record.getCategoryName());
            }
            delete = categoryService.removeById(categoryOrLabel.getId());
        }

        return BaseResponse.ok(delete);

    }


    /**
     * 根据主键id查询标签信息
     *
     * @param categoryOrLabel
     * @return
     */
    @PostMapping("findById")
    public BaseResponse findById(@RequestBody SpiderCategoryOrLabel categoryOrLabel) {

        Integer type = categoryOrLabel.getType();
        if (ObjectUtil.isNull(type)) {
            log.error("参数校验异常！！！传入的type不能为NULL");
            throw new BaseException("参数校验异常！！！传入的type不能为NULL！");
        }

        if (type.equals(SpiderKeyWord.LABEL)) {
            SpiderLabel byId = labelService.getById(categoryOrLabel.getId());
            return BaseResponse.ok(byId);
        }

        if (type.equals(SpiderKeyWord.CATEGORY)) {
            SpiderCategory byId = categoryService.getById(categoryOrLabel.getId());
            return BaseResponse.ok(byId);
        }

        return BaseResponse.ok();
    }


}
