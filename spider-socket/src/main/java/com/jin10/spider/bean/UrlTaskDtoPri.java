package com.jin10.spider.bean;

import com.jin10.spider.common.bean.UrlTaskDto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Airey
 * @date 2020/4/9 18:19
 * ----------------------------------------------
 * 缓存任务的优先级
 * ----------------------------------------------
 */
@Data
@AllArgsConstructor
public class UrlTaskDtoPri implements Comparable<UrlTaskDtoPri> {

    /**
     * 任务实体
     */
    private UrlTaskDto urlTaskDto;

    /**
     * 时间
     */
    private Long time;


    @Override
    public int compareTo(UrlTaskDtoPri o) {
        return this.time > o.getTime() ? 1 : -1;
    }
}
