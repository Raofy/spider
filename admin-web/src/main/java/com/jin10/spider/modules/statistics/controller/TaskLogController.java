package com.jin10.spider.modules.statistics.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.Constants;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.statistics.bean.ServerInfo;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.dto.ProxyResultDto;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.statistics.dto.TemplateResultDto;
import com.jin10.spider.modules.statistics.request.TaskLogPageRequest;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.statistics.service.ITaskLogService;
import com.jin10.spider.modules.statistics.service.ServerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hongda.fang
 * @date 2019-11-26 18:10
 * ----------------------------------------------
 * 日志监控
 */
@RestController
@RequestMapping("/taskLog")
@Slf4j
public class TaskLogController {

    @Autowired
    ITaskLogService taskLogService;

    @Autowired
    ServerInfoService serverInfoService;

    @Autowired
    IDingTalkWarnService warnService;

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 通过taskId查询爬虫日志
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/findByTaskId")
    public BaseResponse findByTaskId(@RequestParam Map<String, Object> params) {
        TaskLog byTaskId = taskLogService.findByTaskId((String) params.get("taskId"));
        return BaseResponse.ok(byTaskId);
    }

    /**
     * 根据条件查询爬虫日志
     *
     * @param request
     * @return
     */
    @PostMapping("/findList")
    public BaseResponse findList(@RequestBody TaskLogPageRequest request) {
        log.info("查询日志参数为: request ==> " + request);
        String proxyIp = request.getProxyIp();
        if (StringUtils.isNotBlank(proxyIp)) {
            String[] split = proxyIp.split(":");
            if (split.length != 2) {
                throw new BaseException("proxyIp参数异常！！！");
            } else {
                request.setProxy(split[0]);
                request.setProxyPort(split[1]);
            }
        }
        request.setOrderField("startTime");
        request.setOrder(Constants.DESC);
        PageUtils pageUtils = taskLogService.queryPage(request);
        return BaseResponse.ok(pageUtils);
    }

    @PostMapping("/addServer12")
    public BaseResponse addServer(@RequestBody Map<String, Object> serverInfo) {

        log.info("params : " + serverInfo);

        JSONObject jsonObject = new JSONObject(serverInfo);
        log.info("json : {}", jsonObject.toJSONString());

        ServerInfo serverInfo1 = jsonObject.toJavaObject(ServerInfo.class);
        log.info("serverInfo: {} ", serverInfo1);

        return BaseResponse.ok(serverInfo);
    }


    /**
     * 添加服务器监控信息
     *
     * @param serverInfo
     * @return
     */
    @RequestMapping(value = "/addServerInfo", method = RequestMethod.POST)
    public BaseResponse saveOrUpdate(@RequestBody ServerInfo serverInfo) {
        ServerInfo save = serverInfoService.save(serverInfo);
        return BaseResponse.ok(save);
    }

    /**
     * 获取服务器信息列表
     *
     * @return
     */
    @GetMapping("getServerInfos")
    public BaseResponse getServerInfos() {
        return BaseResponse.ok(serverInfoService.getServerInfos());
    }

    /**
     * 统计爬虫服务器节点当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */

    @PostMapping("/countSpiderResult")
    public BaseResponse countSpiderResult(@RequestBody TaskLogPageRequest request) {
        List<SpiderRunResultDto> runResultDto = taskLogService.countSpiderRunResult(request);
        return BaseResponse.ok(runResultDto);
    }


    /**
     * 统计 代理ip 当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */

    @PostMapping("/countProxyIp")
    public BaseResponse countProxyIp(@RequestBody TaskLogPageRequest request) {
        List<ProxyResultDto> runResultDto = taskLogService.countProxy(request);
        return BaseResponse.ok(runResultDto);
    }

    /**
     * 统计单个模板 当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */
    @PostMapping("/countTemplateResult")
    public BaseResponse countTemplateResult(@RequestBody TaskLogPageRequest request) {
        log.info("开始查询模板id = { " + request.getTempId() + " } 当天(最近24小时)抓取任务统计结果");
        TemplateResultDto templateResultDto = taskLogService.countTemplateResult(request);
        return BaseResponse.ok(templateResultDto);
    }


    /**
     * 查询代理ip当天(最近24小时)抓取任务列表
     *
     * @param request
     * @return
     */
    @PostMapping("/proxyIpPage")
    public BaseResponse proxyIpPage(@RequestBody TaskLogPageRequest request) {

        String proxyIp = request.getProxyIp();
        if (StringUtils.isNotBlank(proxyIp)) {
            String[] split = proxyIp.split(":");
            if (split.length != 2) {
                throw new BaseException("proxyIp参数异常！！！");
            } else {
                request.setProxy(split[0]);
                request.setProxyPort(split[1]);
            }
        }
        request.setOrderField("startTime");
        request.setOrder(Constants.DESC);
        PageUtils pageUtils = taskLogService.queryPage(request);
        return BaseResponse.ok(pageUtils);
    }

    /**
     * 查询 爬虫节点 当天任务执行详情列表
     *
     * @param request
     * @return
     */

    @PostMapping("/spiderResultPage")
    public BaseResponse queryPage(@RequestBody TaskLogPageRequest request) {
        log.info(JsonUtils2.writeValue(request));
        request.setOrderField("startTime");
        request.setOrder(Constants.DESC);
        PageUtils pageUtils = taskLogService.queryPage(request);
        return BaseResponse.ok(pageUtils);
    }

    /**
     * 查询一个页面当天任务执行详细
     * 如果页面为监控状态，则查询1天的日志
     * 如果页面为停止状态，则查询3天的日志
     *
     * @param request
     * @return
     */
    @PostMapping("findTaskLogDetail")
    public BaseResponse findTaskDetail(@RequestBody @Valid TaskLogPageRequest request) {
        log.info("开始查询 tempId = " + request.getTempId() + " 的任务执行详细！");
        request.setOrderField("creatTime");
        request.setOrder(Constants.DESC);
        // 如果页面为停止状态，则查询3天的日志
        if (!redisUtils.hHasKey(RedisKey.TEMPIDMAP, request.getTempId().toString())) {
            request.setStartTime(DateUtil.offsetDay(new Date(), -3).getTime());
        }
        PageUtils taskLogDetail = taskLogService.queryPage(request);
        return BaseResponse.ok(taskLogDetail);
    }


    /**
     * 查询一段时间内错误日志的详情
     *
     * @param request
     * @return
     */
    @PostMapping("findFailTaskLogList")
    public BaseResponse findFailTaskLogList(@RequestBody TaskLogPageRequest request) {
        log.info("开始查询 失败任务详细! request =" + request);
        request.setOrderField("creatTime");
        request.setOrder(Constants.DESC);
        request.setStatus(-1);
        PageUtils pageUtils = taskLogService.queryPage(request);
        return BaseResponse.ok(pageUtils);
    }


    /**
     * 查询一段时间内维护人员对应的错误日志的详情
     *
     * @param request
     * @return
     */
    @PostMapping("findFailTaskLogByMaintainer")
    public BaseResponse findFailTaskLogByMaintainer(@RequestBody TaskLogPageRequest request) {
        log.info("开始查询维护人员对应的失败任务详细! request =" + request);
        request.setOrderField("creatTime");
        request.setOrder(Constants.DESC);
        request.setStatus(-1);
        PageUtils pageUtils = taskLogService.queryPage(request);
        return BaseResponse.ok(pageUtils);
    }


    /**
     * 上报维护人员完成任务信息
     *
     * @param params
     * @return
     */
    @PostMapping("reportMaintainerInfo")
    public BaseResponse reportMaintainerInfo(@RequestBody Map<String, Object> params) {
        log.info("开始上报维护信息 ==》 params = " + params);
        String maintainer = MapUtil.getStr(params, "maintainer");
        warnService.warnfinishMaintainTask(maintainer);
        return BaseResponse.ok();
    }


    /**
     * 处理后的页面
     *
     * @param taskId
     * @return
     */
    @GetMapping("result/{taskId}")
    public BaseResponse getResult(@PathVariable("taskId") String taskId) {

        log.info("查询 taskId = { " + taskId + " } 的结果页面信息!");
        TaskLog taskLog = taskLogService.findByTaskId(taskId);
        String ip = taskLog.getIp();
        Long tempId = taskLog.getTempId();
        String taskHash = taskLog.getTaskHash();
        String urlTemp = "http://{}:16690/api/snapshot/result/{}/{}";
        String url = StrUtil.format(urlTemp, ip, tempId, taskHash);
        HttpResponse execute = HttpRequest.get(url).execute();
        String body;
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            body = execute.body();
        } else {
            throw new BaseException("访问错误！！！");
        }

        return BaseResponse.ok(body);
    }

    /**
     * 原页面
     *
     * @param taskId
     * @param snapshotHash
     * @return
     */
    @GetMapping("html/{taskId}/{snapshotHash}")
    public BaseResponse getHtml(@PathVariable("taskId") String taskId, @PathVariable("snapshotHash") String snapshotHash) {

        log.info("查询 taskId = { " + taskId + " }  的结果html页面信息!");
        TaskLog taskLog = taskLogService.findByTaskId(taskId);
        String ip = taskLog.getIp();
        Long tempId = taskLog.getTempId();
        String taskHash = taskLog.getTaskHash();
        String urlTemp = "http://{}:16690/api/snapshot/html/{}/{}/{}";
        String url = StrUtil.format(urlTemp, ip, tempId, taskHash, snapshotHash);
        HttpResponse execute = HttpRequest.get(url).execute();
        String body;
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            body = execute.body();
        } else {
            throw new BaseException("访问错误！！！");
        }
        return BaseResponse.ok(body);

    }

    /**
     * 头信息
     *
     * @param taskId
     * @param snapshotHash
     * @return
     */
    @GetMapping("headers/{taskId}/{snapshotHash}")
    public BaseResponse getHeaders(@PathVariable("taskId") String taskId, @PathVariable("snapshotHash") String snapshotHash) {

        log.info("查询 taskId = { \" + taskId + \" }  的头信息! ");

        TaskLog taskLog = taskLogService.findByTaskId(taskId);
        String ip = taskLog.getIp();
        Long tempId = taskLog.getTempId();
        String taskHash = taskLog.getTaskHash();
        String urlTemp = "http://{}:16690/api/snapshot/resp_headers/{}/{}/{}";
        String url = StrUtil.format(urlTemp, ip, tempId, taskHash, snapshotHash);
        HttpResponse execute = HttpRequest.get(url).execute();
        String body;
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            body = execute.body();
        } else {
            throw new BaseException("访问错误！！！");
        }
        return BaseResponse.ok(body);


    }


}
