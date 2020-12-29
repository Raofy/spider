package com.jin10.spider.modules.template.dto;

import cn.hutool.core.collection.CollUtil;
import com.jin10.spider.common.bean.UrlTask;
import lombok.Data;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-28 11:26
 * ----------------------------------------------
 * TaskQueue 信息
 */

@Data
public class TaskQueueInfo {
    private Integer queueSize;

    private UrlTask task;

    private List<UrlTask> taskList;




    public void setValue(List<UrlTask> tasks, Long tempId, boolean showAll) {
        if (tempId != null) {
            if (CollUtil.isNotEmpty(tasks)) {
                for (UrlTask item : tasks) {
                    if (tempId.equals(item.getTempId())) {
                        task = item;
                        break;
                    }
                }
            }
        }
        if (showAll) {
            if (tasks != null) {
                taskList = tasks;
                queueSize = tasks.size();

            }
        }
    }

}
