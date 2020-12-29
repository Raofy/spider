package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderLabel;
import com.jin10.spider.spiderserver.service.ISpiderLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 爬虫消息标签库 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-27
 */
@RestController
@RequestMapping("/spider-label")
@Slf4j
public class SpiderLabelController {

    @Autowired
    private ISpiderLabelService labelService;

    /**
     * 查询爬虫标签库列表
     *
     * @return
     */
    @GetMapping("list")
    public BaseResponse list() {
        log.info("查询爬虫标签库");
        List<SpiderLabel> list = labelService.list();
        return BaseResponse.ok(list);
    }

    /**
     * 新增爬虫标签
     *
     * @return
     */
    @PostMapping("add")
    public BaseResponse add(@RequestBody @Valid SpiderLabel label, BindingResult result) {
        log.info("新增爬虫消息标签为： {}", label);
        if (result.hasErrors()) {
            log.error("参数校验异常！ ==> {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            return BaseResponse.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        boolean save = labelService.save(label);
        DataCache.labelMap.put(label.getLabelName(), label.getLabelColor());
        return BaseResponse.ok(save);
    }

    /**
     * 更新爬虫标签
     *
     * @param label
     * @return
     */
    @PostMapping("update")
    public BaseResponse update(@RequestBody @Valid SpiderLabel label, BindingResult result) {
        log.info("更新爬虫标签为 ：{}", label);
        if (result.hasErrors()) {
            log.error("参数校验异常！ ==> {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            return BaseResponse.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        SpiderLabel record = labelService.getById(label.getId());
        DataCache.labelMap.remove(record.getLabelName());
        boolean b = labelService.updateById(label);
        DataCache.labelMap.put(label.getLabelName(), label.getLabelColor());
        return BaseResponse.ok(b);
    }

    /**
     * 根据主键id删除爬虫标签
     *
     * @param id
     * @return
     */
    @GetMapping("delete/{id}")
    public BaseResponse delete(@PathVariable("id") Long id) {
        log.info("删除主键id 为 { " + id + " } 的标签记录");
        SpiderLabel label = labelService.getById(id);
        DataCache.labelMap.remove(label.getLabelName());
        boolean b = labelService.removeById(id);
        return BaseResponse.ok(b);
    }


    /**
     * 根据主键id查询标签信息
     * @param id
     * @return
     */
    @GetMapping("findById/{id}")
    public BaseResponse findById(@PathVariable("id") Long id) {

        log.info("根据主键查询标签信息, id={}", id);
        SpiderLabel byId = labelService.getById(id);
        return BaseResponse.ok(byId);
    }


}
