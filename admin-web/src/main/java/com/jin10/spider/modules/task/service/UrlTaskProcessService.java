package com.jin10.spider.modules.task.service;

import cn.hutool.core.collection.CollUtil;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.Constants;
import com.jin10.spider.common.utils.GlobalCache;
import com.jin10.spider.common.utils.JsonUtil;
import com.jin10.spider.modules.statistics.bean.BatchProductQueueInfo;
import com.jin10.spider.modules.statistics.bean.TaskProductWarn;
import com.jin10.spider.modules.statistics.dto.TaskQueueInfoDto;
import com.jin10.spider.modules.statistics.handler.PushWebMsgSocketHandler;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.service.impl.UrlTaskMangerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hongda.fang
 * @date 2019-11-06 15:12
 * ----------------------------------------------
 */
@Service
public class UrlTaskProcessService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private UrlTaskMangerImpl taskManger;
    @Autowired
    private IIpProxyProcessService proxyProcessService;
    @Autowired
    private PushWebMsgSocketHandler pushWebMsgSocketHandler;
    @Autowired
    private IDingTalkWarnService warnService;

    /**
     * 需要爬去的队列
     */
    private PriorityBlockingQueue<UrlTask> queue = GlobalCache.getProductQueue();
    /**
     * 需要爬去的队列
     */
    private PriorityBlockingQueue<UrlTask> queueForeign = GlobalCache.getProductQueueForeign();

    /**
     * 已经生产的任务
     */
    private ConcurrentHashMap<String, UrlTask> productTaskMap = GlobalCache.getProductTaskMap();
    /**
     * 记录任务被领取的时间
     */
    private ConcurrentHashMap<String, Date> getTaskMap = GlobalCache.getProductGetedDateTaskMap();

    private int productTimes = 0;

    /**
     * 警告消息
     */
    private TaskProductWarn taskProductWarn;


    /**
     * 产生task 时间间隔
     */
    private final long CREATE_TASKS_TIME = 1000;
    /**
     * 每次获取task 列表 大小
     */
    private final int GET_TASK_MAX_ITEM = 1;


    /**
     * 初始化 temp
     * 启动socket 监听
     * 启动线程 生产任务
     */
   public void startRunCreatTask() {
        //初始化模板
        taskManger.initTemplate();
        taskScheduler.scheduleAtFixedRate(this::startTask, CREATE_TASKS_TIME);
    }

    /**
     *
     *
     *
     * 启动生成任务
     */
    private void startTask() {
        try {
            List<UrlTask> tasks = taskManger.creatTask();
            offerTask(tasks);
        } catch (Exception e) {
            logger.error("startRunCreatTask ====" + e);
        }
    }

    /**
     * 向队列添加 task
     *
     * @param tasks
     */
    public void offerTask(List<UrlTask> tasks) {
        /**
         * 发送当前队列信息
         */
        pushWebMsgSocketHandler.pushTaskQueueInfo();
        if (CollUtil.isNotEmpty(tasks)) {
            int preQueueSize = queue.size() + queueForeign.size();
            List<UrlTask> addTasks = new ArrayList<>();
            for (UrlTask task : tasks) {
                task.setPuded(true);
                if (!task.isWeForeign()) {
                    if (!queue.contains(task)) {
                        queue.offer(task);
                        addTasks.add(task);
                        logger.info("产生任务" + Constants.LOGGER_STRING + JsonUtil.toJson(task));
                    } else {
                        task.setPuded(false);
                    }
                } else {
                    if (!queueForeign.contains(task)) {
                        queueForeign.offer(task);
                        addTasks.add(task);
                        logger.info("产生任务" + Constants.LOGGER_STRING + JsonUtil.toJson(task));
                    } else {
                        task.setPuded(false);
                    }
                }
            }
            //检查队列消息
            checkWarn(preQueueSize, queue.size() + queueForeign.size());
            //推送到任务监控中
            pushProductTaskMsg(addTasks);
        }
        productTimes = productTimes + 1;
        Map<String, UrlTask> productMap = tasks.stream().collect(Collectors.toMap(UrlTask::getTaskUuid, Function.identity(), (key1, key2) -> key2));
        productTaskMap.putAll(productMap);
    }

    private void pushProductTaskMsg(List<UrlTask> tasks) {
        if (CollUtil.isNotEmpty(tasks)) {
            tasks.forEach(task -> {
               task.setTemp(taskManger.findTempByCurMap(task));
            });
            pushWebMsgSocketHandler.pushProductTask(tasks);
        }
    }


    /**
     * 检查队列
     *
     * @param preSize
     * @param curSize
     */
    private void checkWarn(int preSize, int curSize) {

        if (preSize == curSize && curSize > 0) {
            if (taskProductWarn == null) {
                taskProductWarn = new TaskProductWarn(Constant.WARN_LIMIT.TASK_KEEP_TIME, Constant.WARN_LIMIT.TASK_KEEP_TIME * 5, Constant.WARN_LIMIT.TASK);
                taskProductWarn.addTimes();
            }
            taskProductWarn.addTimes();
            if (taskProductWarn.canPush()) {
                taskProductWarn.setCurQueueSize(curSize);
                warnService.taskProductWarn(taskProductWarn);
            }
        } else {
            taskProductWarn = null;
        }

    }


    /**
     * 获取 任务 list
     *
     * @param isForeign
     * @return
     */
    public List<UrlTaskDto> getTasks(boolean isForeign) {
        List<UrlTaskDto> tasks = new ArrayList<>();
        Date curDate = new Date();
        PriorityBlockingQueue<UrlTask> myQueue = isForeign ? queueForeign : queue;
        int loopNum = GET_TASK_MAX_ITEM < myQueue.size() ? GET_TASK_MAX_ITEM : myQueue.size();
        for (int i = 0; i < loopNum; i++) {
            UrlTask poll = myQueue.poll();
            if (poll != null) {
                getTaskMap.put(poll.getTaskUuid(), curDate);
            }
            UrlTaskDto dto = taskManger.convertAddTemp(poll);
            if (dto != null) {
                tasks.add(dto);
            } else {
                logger.error(" UrlTaskDto is null");
            }
        }

        try {
            proxyProcessService.takeProxyIp(tasks);
        } catch (Exception ex) {
            logger.error("  takeProxyIp fail ", ex);
        }

//        logger.info(Constants.LOGGER_STRING + "getTasks=====" + JsonUtil.toJson(tasks));


        return tasks;
    }

    /**
     * 移除任务
     *
     * @param tempId
     * @return
     */
    public boolean removeTask(Long tempId) {
        UrlTask urlTask = new UrlTask();
        urlTask.setTempId(tempId);
        boolean remove = queueForeign.remove(urlTask) || queueForeign.remove(urlTask);
        logger.warn(" remove task temp id is " + tempId + "====" + remove);
        return remove;
    }


    public List<UrlTask> getQueueList() {
        return new ArrayList<>(queue);
    }

    public List<UrlTask> getQueueForeignList() {
        return new ArrayList<>(queueForeign);
    }

    public BatchProductQueueInfo getProductQueueInfo() {
        BatchProductQueueInfo info = new BatchProductQueueInfo();
        info.setGetTaskMap(new ConcurrentHashMap<>(getTaskMap));
        getTaskMap.clear();

        info.setProductTaskMap(new ConcurrentHashMap<>(productTaskMap));
        productTaskMap.clear();
        info.setTimes(productTimes);
        productTimes = 0;
        info.setCurQueueSize(queue.size());
        info.setCurForeignQueueSize(queueForeign.size());
        return info;
    }


    public TaskQueueInfoDto getQueueInfo(boolean isForeign) {
        TaskQueueInfoDto taskQueue = new TaskQueueInfoDto();
        taskQueue.setForeign(isForeign);
        PriorityBlockingQueue<UrlTask> cur = null;
        if (isForeign) {
            cur = queueForeign;
        } else {
            cur = queue;
        }
        taskQueue.setTaskSize(cur.size());
        return taskQueue;
    }


    /**
     * 获取队列最后 100 条任务，提供给 监控端
     *
     * @return
     */

    public List<UrlTask> getQueueDetailToPush() {
        List<UrlTask> urlTasks = new ArrayList<>(queue);
        List<UrlTask> foreignUrlTasks = new ArrayList<>(queueForeign);

        List<UrlTask> tasks = new ArrayList<>();
        tasks.addAll(urlTasks);
        tasks.addAll(foreignUrlTasks);

        if (CollUtil.isNotEmpty(tasks)) {
            int size = tasks.size();
            if (size > 100) {
                tasks = tasks.subList(size - 100, size);
            }
            tasks.forEach(task -> {
                task.setTemp(taskManger.findTempByCurMap(task));
            });
        }
        return tasks;
    }


    public TaskProductWarn getTaskProductWarn() {
        return taskProductWarn;
    }
}
