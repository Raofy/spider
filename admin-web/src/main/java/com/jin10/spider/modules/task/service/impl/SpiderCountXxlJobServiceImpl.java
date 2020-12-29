package com.jin10.spider.modules.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.config.CustomConfig;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.statistics.request.TaskLogPageRequest;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.statistics.service.ITaskLogService;
import com.jin10.spider.modules.task.entity.SpiderCountJob;
import com.jin10.spider.modules.task.service.ISpiderCountJobService;
import com.jin10.spider.modules.task.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Airey
 * @date 2020/3/8 21:11
 * ----------------------------------------------
 * 执行定时统计每小时爬虫任务概览
 * ----------------------------------------------
 */
@Service
@Slf4j
public class SpiderCountXxlJobServiceImpl implements XxlJobService {


    @Autowired
    private ITaskLogService taskLogService;

    @Autowired
    private IDingTalkWarnService warnService;

    @Autowired
    private ISpiderCountJobService spiderCountJobService;

    @Autowired
    private CustomConfig customConfig;


    /**
     * 执行xxl-job调度任务
     *
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob(value = "spiderCountJobHandler")
    @Override
    public ReturnT<String> execute(String params) throws Exception {


        XxlJobLogger.log("开始执行定时统计任务！！！！");
        TaskLogPageRequest request = new TaskLogPageRequest();
        Date curTime = new Date();
        request.setEndTime(curTime.getTime());
        request.setStartTime(DateUtil.offsetHour(curTime, -1).getTime());
        List<SpiderRunResultDto> spiderRunResultDtoList = taskLogService.countSpiderRunResult(request);
        try {
            if (CollUtil.isNotEmpty(spiderRunResultDtoList)) {
                SpiderRunResultDto spiderRunResultDto = getTotalCount(spiderRunResultDtoList);

                spiderRunResultDtoList.add(spiderRunResultDto);
                List<SpiderCountJob> spiderCountJobArrayList = new ArrayList<>();
                spiderRunResultDtoList.forEach(item -> {
                    SpiderCountJob spiderCountJob = new SpiderCountJob();
                    BeanUtils.copyProperties(item, spiderCountJob);
                    spiderCountJob.setAllCount(item.getAll());
                    spiderCountJob.setSuccessCount(item.getSuccess());
                    spiderCountJob.setFailCount(item.getFail());
                    spiderCountJob.setSuccessRate(String.format("%.3f", item.getSuccessRate() * 100) + "%");
                    spiderCountJobArrayList.add(spiderCountJob);
                });
                spiderCountJobService.saveBatch(spiderCountJobArrayList);
                warnService.taskLogCountWarn(spiderRunResultDto);
                warnMaintainer(DateUtil.offsetHour(curTime, -1), curTime);

            }
        } catch (Exception e) {
            log.error("执行定时任务错误！！！", e);

        }

        XxlJobLogger.log("执行定时统计任务成功！！！！");

        return ReturnT.SUCCESS;
    }


    /**
     * 计数总的统计量
     *
     * @param spiderRunResultDtoList
     * @return
     */
    private SpiderRunResultDto getTotalCount(List<SpiderRunResultDto> spiderRunResultDtoList) {

        long all = 0L;
        long success = 0L;
        long fail = 0L;
        long temp = 0L;

        SpiderRunResultDto total = new SpiderRunResultDto();

        for (SpiderRunResultDto item : spiderRunResultDtoList) {
            all += item.getAll();
            success += item.getSuccess();
            fail += item.getFail();
            if (temp < item.getTemp()) {
                temp = item.getTemp();
            }
            total.setStartTime(item.getStartTime());
            total.setEndTime(item.getEndTime());
        }

        total.setTemp(temp);
        total.setIp("total");
        total.setAll(all);
        total.setSuccess(success);
        total.setFail(fail);
        String successCount = Long.toString(success);
        String allCount = Long.toString(all);
        total.setSuccessRate(NumberUtil.div(successCount, allCount, 5).doubleValue());
        return total;
    }


    /**
     * 通知维护人员
     *
     * @param startTime
     * @param endTime
     */
    public void warnMaintainer(Date startTime, Date endTime) {

        int hour = DateUtil.hour(startTime, true);
        //提醒时间为9点到18点
        if (hour > 18 || hour < 9) {
            return;
        }
        String realUrl = customConfig.serverAddress + "findMaintainerList";
        BaseResponse baseResponse = getBaseResponse(realUrl);
        if (HttpStatus.HTTP_OK == baseResponse.getCode()) {
            if (baseResponse.getData() instanceof JSONArray) {
                JSONArray array = (JSONArray) baseResponse.getData();
                if (!array.isEmpty()) {
                    warnService.warnMaintainer(array, startTime, endTime);
                }
            }
        }

    }

    /**
     * 获取返回响应体
     *
     * @param realUrl
     * @return
     */
    private BaseResponse getBaseResponse(String realUrl) {
        BaseResponse response = new BaseResponse();
        HttpResponse execute = HttpRequest.get(realUrl).timeout(5000).execute();
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            String body = execute.body();
            if (JSONObject.isValid(body)) {
                JSONObject resultJson = JSONObject.parseObject(body);
                response = resultJson.toJavaObject(BaseResponse.class);
            }
        } else {
            return BaseResponse.error(execute.getStatus(), execute.body());
        }
        return response;
    }


}
