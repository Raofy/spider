package com.jin10.spider.modules.task.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

/**
 * <p>
 * 每小时执行任务统计
 * </p>
 *
 * @author Airey
 * @since 2020-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderCountJob implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId
    private Long id;
    /**
     * ip地址
     */
    private String ip;

    /**
     * 总任务数量
     */
    private Long allCount;

    /**
     * 成功数量
     */
    private Long successCount;

    /**
     * 失败数量
     */
    private Long failCount;

    /**
     * 成功率
     */
    private String successRate;

    /**
     * 模板数量
     */
    private Long temp;

    /**
     * 开始统计时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 入库时间
     */
    private Date insertTime;


}
