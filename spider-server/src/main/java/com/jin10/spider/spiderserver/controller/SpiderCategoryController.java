package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderCategory;
import com.jin10.spider.spiderserver.service.ISpiderCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 消息类别管理 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-29
 */
@RestController
@RequestMapping("/spider-category")
@Slf4j
public class SpiderCategoryController {


    @Autowired
    private ISpiderCategoryService categoryService;


    /**
     * 查询类别列表
     *
     * @return
     */
    @GetMapping("list")
    public BaseResponse list() {
        log.info("开始获取消息类别列表");
        List<SpiderCategory> list = categoryService.list(null);
        return BaseResponse.ok(list);
    }

    /**
     * 查询所有分类，前端列表
     *
     * @return
     */
    @GetMapping("frontList")
    public BaseResponse frontList() {
        log.info("开始获取前端消息类别列表");
        List<SpiderCategory> list = categoryService.frontList();
        return BaseResponse.ok(list);
    }

    /**
     * 新增消息类别
     *
     * @param spiderCategory
     * @return
     */
    @PostMapping("add")
    public BaseResponse add(@RequestBody @Valid SpiderCategory spiderCategory, BindingResult result) {
        log.info("新增消息类别的参数为 = {}", spiderCategory);
        if (result.hasErrors()) {
            log.error("参数校验异常！ ==> {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            return BaseResponse.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        boolean save = categoryService.save(spiderCategory);
        DataCache.categoryMap.put(spiderCategory.getCategoryName(), spiderCategory.getCategoryColor());
        return BaseResponse.ok(save);
    }

    /**
     * 更新消息类别
     *
     * @param spiderCategory
     * @return
     */
    @PostMapping("update")
    public BaseResponse update(@RequestBody @Valid SpiderCategory spiderCategory, BindingResult result) {
        log.info("修改消息类别的参数为 = {}", spiderCategory);
        if (result.hasErrors()) {
            log.error("参数校验异常！ ==> {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            return BaseResponse.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        SpiderCategory record = categoryService.getById(spiderCategory.getCateId());
        DataCache.categoryMap.remove(record.getCategoryName());
        boolean b = categoryService.updateById(spiderCategory);
        DataCache.categoryMap.put(spiderCategory.getCategoryName(), spiderCategory.getCategoryColor());
        return BaseResponse.ok(b);
    }

    /**
     * 根据主键id删除记录
     *
     * @param cateId
     * @return
     */
    @GetMapping("delete/{cateId}")
    public BaseResponse delete(@PathVariable("cateId") Long cateId) {
        log.info("开始删除主键cateId =  { " + cateId + " } 的消息类别记录！");
        SpiderCategory record = categoryService.getById(cateId);
        DataCache.categoryMap.remove(record.getCategoryName());
        boolean b = categoryService.removeById(cateId);
        return BaseResponse.ok(b);
    }


    /**
     * 根据主键id查询类别信息
     *
     * @param cateId
     * @return
     */
    @GetMapping("findById/{cateId}")
    public BaseResponse findById(@PathVariable("cateId") Long cateId) {

        log.info("根据主键查询类别信息, cateId={}", cateId);
        SpiderCategory byId = categoryService.getById(cateId);
        return BaseResponse.ok(byId);
    }


}
