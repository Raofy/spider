package com.jin10.spider.modules.task.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.bean.IdRequest;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.*;
import com.jin10.spider.common.validator.ValidatorUtils;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.request.IpInfoPageRequest;
import com.jin10.spider.modules.task.request.VerifyIpRequest;
import com.jin10.spider.modules.task.service.IIpInfoService;
import com.jin10.spider.modules.task.service.impl.IpProxyProcessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 代理ip 信息 前端控制器
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-31
 */
@RestController
@RequestMapping("/ipInfo")
@Slf4j
public class IpInfoController {

    @Autowired
    private IIpInfoService ipInfoService;


    @Autowired
    private IpProxyProcessServiceImpl proxyProcessService;

    @Autowired
    private CurlUtils curlUtils;


    /**
     * 查询Ip代理列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("/list")
    public BaseResponse list(@RequestBody IpInfoPageRequest params) {
        PageUtils page = ipInfoService.queryPage(params);
        return BaseResponse.ok(page);
    }

    /**
     * 删除代理Ip
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody IdRequest request) {
        ValidatorUtils.validateEntity(request);
        boolean delete = ipInfoService.removeById(request.getId());
        return BaseResponse.ok(delete);
    }


    /**
     * 添加ip代理
     *
     * @param ipInfo
     * @return
     */
    @PostMapping("/add")
    public BaseResponse add(@RequestBody @Valid IpInfo ipInfo, HttpServletRequest servletRequest) {
        log.info("新增ip参数为:  ipInfo ==> {}", ipInfo);
        String remoteAddr = IPUtils.getIpAddr(servletRequest);
        log.info("remoteAdder: " + remoteAddr);

        boolean addFlag = ipInfoService.add(ipInfo);
        return BaseResponse.ok(addFlag);
    }

    /**
     * 批量添加代理Ip,暂时不检验
     *
     * @param request
     * @return
     */
    @PostMapping("/addBatch")
    public BaseResponse addBatch(@RequestBody List<IpInfo> request) {
        List<IpInfo> list = ipInfoService.list();
        List<IpInfo> collect = request.stream().filter(item -> !list.contains(item)).map(item -> item.setWheVaild(true)).collect(Collectors.toList());
        boolean b = ipInfoService.saveBatch(collect);
        return BaseResponse.ok(b);
    }


    /**
     * 校验ip是否可用
     *
     * @param request
     * @return
     */
    @PostMapping("/verifyIp")
    public BaseResponse verifyIp(@RequestBody VerifyIpRequest request) {
        ValidatorUtils.validateEntity(request);
        JSONObject extra;
        if (JSONObject.isValidObject(request.getExtra())) {
            extra = JSONObject.parseObject(request.getExtra());
        } else {
            throw new BaseException("extra 参数错误！！！");
        }

        boolean isValid = false;
        if (extra.size() == 0) {
            isValid = ipInfoService.verifyIp(request.getIp(), request.isWheForeign());
        } else {
            if (extra.containsKey("auth")) {
                VerifyIpRequest.AuthRequest authRequest = extra.getObject("auth", VerifyIpRequest.AuthRequest.class);
                isValid = ipInfoService.verifyIpByAuth(request.getIp(), authRequest, request.isWheForeign());
            }
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("isValid", isValid);
        return BaseResponse.ok(map);
    }



    /**
     * 获取IP有效信息
     *
     * @return
     */
    @GetMapping("/getIpInfo")
    public BaseResponse getIpInfo() {

        Long highLevel = ipInfoService.selectCount(Constant.TEMPLATE.PROXY_LEVEL_HIGH);
        Long lowLevel = ipInfoService.selectCount(Constant.TEMPLATE.PROXY_LEVEL_LOW);
        Map<String, Long> countMap = new HashMap<>(3);
        countMap.put("all", highLevel + lowLevel);
        countMap.put("high", highLevel);
        countMap.put("low", lowLevel);
        return BaseResponse.ok(countMap);
    }


    /**
     * 根据平台统计IP数量
     *
     * @return
     */
    @GetMapping("getIpInfoByPlatform")
    public BaseResponse getIpInfoByPlatform() {

        LambdaQueryWrapper<IpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IpInfo::isWheVaild, true);
        List<IpInfo> list = ipInfoService.list(queryWrapper);
        Map<String, Object> platformResultMap = new HashMap<>();
        Map<@NotNull String, List<IpInfo>> platformMap = list.stream().collect(Collectors.groupingBy(IpInfo::getPlatform));
        if (CollUtil.isNotEmpty(platformMap)) {
            platformMap.forEach((k, v) -> {
                Map<String, Long> tempMap = new HashMap<>(3);
                long low = v.stream().filter(item -> item.getProxyLevel() == 2).count();
                long high = v.stream().filter(item -> item.getProxyLevel() == 3).count();
                tempMap.put("high", high);
                tempMap.put("low", low);
                tempMap.put("all", (long) v.size());
                platformResultMap.put(k, tempMap);
            });
        }
        return BaseResponse.ok(platformResultMap);

    }

    /**
     * 获取平台厂商信息列表
     *
     * @return
     */
    @GetMapping("getPlatform")
    public BaseResponse getPlatform() {
        List<IpInfo> list = ipInfoService.list();
        Set<@NotNull String> platformSet = list.stream().map(IpInfo::getPlatform).collect(Collectors.toSet());
        Map<String, Object> platMap = new HashMap<>();
        platMap.put("platform", platformSet);
        return BaseResponse.ok(platMap);
    }

    /**
     * 批量获取指定条件的代理ip
     *
     * @param params
     * @return
     */
    @PostMapping("getProxyIpBatch")
    public BaseResponse getProxyIpBatch(@RequestBody Map<String, Object> params) {
        log.info("批量获取指定代理ip, params ={} ", params);
        Integer proxyLevel = MapUtil.getInt(params, "proxyLevel");
        Integer count = MapUtil.getInt(params, "count");
        if (ObjectUtil.isNull(proxyLevel) && ObjectUtil.isNotNull(count)) {
            throw new BaseException("传递的参数异常！！！");
        }
        LambdaQueryWrapper<IpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IpInfo::getProxyLevel, proxyLevel).eq(IpInfo::isWheVaild, true).last("limit 0," + count).orderByDesc(IpInfo::getCreateTime);
        List<IpInfo> ipInfoList = ipInfoService.list(queryWrapper);
        Map<String, Object> map = new HashMap<>(1);
        map.put("ipInfoList", ipInfoList);
        return BaseResponse.ok(map);
    }


    /**
     * 手动移除失效的代理IP
     *
     * @param ipInfo
     * @return
     */
    @PostMapping("removeInvalidIp")
    public BaseResponse removeInvalidIp(@RequestBody IpInfo ipInfo) {
        log.info("开始处理失效Ip : " + ipInfo);
        IpInfo info = ipInfoService.findByIp(ipInfo.getIp());
        if (ObjectUtil.isNull(info)) {
            throw new BaseException("代理ip: " + ipInfo.getIp() + " 已经不存在代理池中！！！ ");
        }
        try {
            proxyProcessService.updateIpQueue(info);
        } catch (Exception e) {
            log.error("更新代理池异常！！！ ", e);
        }
        return BaseResponse.ok();
    }


}
