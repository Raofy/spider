package com.jin10.spider.modules.template.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jin10.spider.common.annotation.SysLog;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.bean.IdBatchRequest;
import com.jin10.spider.common.bean.IdRequest;
import com.jin10.spider.common.utils.AuthorityUtils;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.validator.ValidatorUtils;
import com.jin10.spider.modules.template.dto.TaskQueueInfo;
import com.jin10.spider.modules.template.dto.TestTempDto;
import com.jin10.spider.modules.template.entity.StopTemplateReason;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.request.DeleteTempsRequest;
import com.jin10.spider.modules.template.request.RenewalRequest;
import com.jin10.spider.modules.template.request.TemplatePageRequest;
import com.jin10.spider.modules.template.service.ITemplateService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 爬虫模版 前端控制器
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
@RestController
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private ITemplateService templateService;


    private Logger logger = LoggerFactory.getLogger(getClass());

    @SysLog("保存/更新模版")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public BaseResponse saveOrUpdate(@RequestBody Template template, @RequestHeader(name = Constant.JWT_KEY.HEADER, required = false) String token) {
        logger.info(JsonUtils2.writeValue(template));
        ValidatorUtils.validateEntity(template);
        ValidatorUtils.validateEntity(template.getTempRule());
        ValidatorUtils.validateEntity(template.getDetailTempRule());
        String validedUserFromToken = AuthorityUtils.getValidedUserFromToken(token);
        if (validedUserFromToken != null) {
            if (template.getId() != null && template.getId() > 0) {
                template.setUpdateUser(validedUserFromToken);
                template.setUpdateTime(new Date());
            } else {
                template.setCreateUser(validedUserFromToken);
            }
        }
        Template template1 = templateService.saveTemp(template);
        return BaseResponse.ok(template1);
    }

    /**
     * 更新备注
     *
     * @param template
     * @return
     */
    @PostMapping(value = "/updateRemark")
    public BaseResponse updateRemark(@RequestBody Template template) {
        Long id = template.getId();
        String remark = template.getRemark();
        logger.info("开始更新模板 id = {},remark= {}", id, remark);
        boolean b = templateService.updateRemark(id, remark);
        return BaseResponse.ok(b);
    }

    @SysLog("查询模版list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse list(@RequestBody TemplatePageRequest params) {
        logger.info(JsonUtils2.writeValue(params));
        PageUtils page = templateService.queryPage(params);
        return BaseResponse.ok(page);
    }


    /**
     * 过期模板列表
     *
     * @param params
     * @return
     */
    @SysLog("查询过期模版list")
    @PostMapping(value = "/expireList")
    public BaseResponse expireList(@RequestBody TemplatePageRequest params) {
        logger.info(JsonUtils2.writeValue(params));
        if (params.getExpireTime() == null) {
            params.setExpireTime(new Date());
        }
        PageUtils page = templateService.queryPage(params);
        return BaseResponse.ok(page);
    }

    @SysLog("通过ID 查询 模版")
    @ResponseBody
    @RequestMapping("/findById")
    public BaseResponse findById(@RequestParam Map<String, Object> params) {
        String idStr = (String) params.get("id");
        Template id = templateService.findById(Long.valueOf(idStr), true);
        return BaseResponse.ok(id);
    }

    @SysLog("删除模版")
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody IdRequest request, @RequestHeader(name = Constant.JWT_KEY.HEADER, required = false) String token) {
        ValidatorUtils.validateEntity(request);
        String validedUserFromToken = AuthorityUtils.getValidedUserFromToken(token);
        templateService.updateDeleteNameById(request.getId(), validedUserFromToken);
        boolean delete = templateService.delete(request.getId());
        return BaseResponse.ok(delete);
    }

    @SysLog("删除模版list")
    @PostMapping("/deletes")
    public BaseResponse deletes(@RequestBody DeleteTempsRequest request, @RequestHeader(name = Constant.JWT_KEY.HEADER, required = false) String token) {
        ValidatorUtils.validateEntity(request);
        List<Long> ids = request.getIds();
        if (CollUtil.isNotEmpty(ids)) {
            String validedUserFromToken = AuthorityUtils.getValidedUserFromToken(token);

            ids.forEach(id -> {
                templateService.updateDeleteNameById(id, validedUserFromToken);
            });

        }
        boolean delete = templateService.deletes(request.getIds());
        return BaseResponse.ok(delete);
    }

    @SysLog("停止模版")
    @PostMapping("/stop")
    public BaseResponse stop(@RequestBody IdRequest request, @RequestHeader(name = Constant.JWT_KEY.HEADER, required = false) String token) {
        ValidatorUtils.validateEntity(request);
        String validedUserFromToken = AuthorityUtils.getValidedUserFromToken(token);
        templateService.updateDeleteNameById(request.getId(), validedUserFromToken);
        boolean stop = templateService.stop(request.getId());
        if (stop) {
            LambdaUpdateWrapper<Template> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Template::getId, request.getId()).set(Template::getStopTime, new Date())
                    .set(Template::getStopReason, StrUtil.format(StopTemplateReason.PERSON_OPTION, validedUserFromToken));
            templateService.update(null, updateWrapper);
        }
        return BaseResponse.ok(stop);
    }

    @SysLog("启动模版")
    @PostMapping("/start")
    public BaseResponse start(@RequestBody IdRequest request) {
        ValidatorUtils.validateEntity(request.getId());
        boolean start = templateService.start(request.getId());
        return BaseResponse.ok(start);
    }


    /**
     * 批量启动模板
     *
     * @param idBatchRequest
     * @return
     */
    @PostMapping("/startBatch")
    public BaseResponse startBatch(@RequestBody IdBatchRequest idBatchRequest) {
        boolean b = templateService.startBatch(idBatchRequest.getIdList());
        return BaseResponse.ok(b);
    }


    /**
     * 模板续期，将过期模板重新运行，并重新设置过期时间
     *
     * @return
     */
    @PostMapping("/renewal")
    public BaseResponse renewal(@RequestBody RenewalRequest renewalRequest) {
        boolean renewal = templateService.renewal(renewalRequest);
        return BaseResponse.ok(renewal);
    }

    @SysLog("刷新模版")
    @PostMapping("/refresh")
    public BaseResponse refresh() {
        templateService.initTemplate();
        return BaseResponse.ok(null);
    }


    /**
     * 测试模版
     *
     * @param template
     * @return
     */
    @SysLog("测试模版")
    @PostMapping("/testTemp")
    public BaseResponse testTemp(@RequestBody Template template) {
        logger.info(JsonUtils2.writeValue(template));
        ValidatorUtils.validateEntity(template);
        ValidatorUtils.validateEntity(template.getTempRule());
        ValidatorUtils.validateEntity(template.getDetailTempRule());

        TestTempDto tempDto = templateService.testTemp(template);
        return testTempGetResponse(tempDto);
    }

    @SysLog("通过ID测试模版")
    @PostMapping("/testTempById")
    public BaseResponse testTemp(@RequestBody IdRequest idRequest) {
        logger.info(JsonUtils2.writeValue(idRequest));
        ValidatorUtils.validateEntity(idRequest);
        Template template = templateService.findById(idRequest.getId(), true);

        TestTempDto tempDto = templateService.testTemp(template);
        return testTempGetResponse(tempDto);
    }

    private BaseResponse testTempGetResponse(TestTempDto tempDto) {
        List<TestTempDto.ResultEntity> result = tempDto.getResult();

        /**
         * 0    # 提取不到结果，可能页面没数据，也可能是提取规则有问题
         *         1    # 有结果且期间没出现异常
         *         2    # 处理过程中出现异常
         */
        if (tempDto.getCode() == 1) {
            return BaseResponse.ok(result);
        } else {
            return BaseResponse.error(tempDto.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping("/taskQueueInfo")
    public BaseResponse taskQueueInfo(@RequestParam Map<String, Object> params) {
        String showAll = (String) params.get("showAll");
        boolean show = true;
        if (StringUtils.isNotEmpty(showAll)) {
            show = Boolean.valueOf(showAll);
        }
        String tempId = (String) params.get("tempId");
        Long id = null;
        if (StringUtils.isNotEmpty(tempId)) {
            id = Long.valueOf(tempId);
        }
        List<TaskQueueInfo> queueInfos = templateService.taskQueueInfos(show, id);
        return BaseResponse.ok(queueInfos);
    }


    /**
     * 通过网址(pagesite)判断源是否存在
     *
     * @return
     */
    @PostMapping("/isExist")
    public BaseResponse isTemplate(@RequestBody Map<String, Object> paramMap) {

        boolean flag = true;
        String pageSite = MapUtil.getStr(paramMap, "pageSite");
        List<Template> list = templateService.lambdaQuery().eq(Template::getPageSite, pageSite).list();
        if (CollectionUtils.isEmpty(list)) {
            flag = false;
        }
        return BaseResponse.ok(flag);

    }


    /**
     * 获取tempId列表
     *
     * @return
     */
    @GetMapping("getTemplateIdList")
    public BaseResponse getTemplateIdList() {
        LambdaQueryWrapper<Template> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Template::getDomainName);
        List<Template> templateList = templateService.list(queryWrapper);
        List<Long> templateIdList = templateList.stream().map(Template::getId).collect(Collectors.toList());
        HashMap<String, Object> tempMap = new HashMap<>();
        tempMap.put("tempIdList", templateIdList);
        return BaseResponse.ok(tempMap);
    }


}
