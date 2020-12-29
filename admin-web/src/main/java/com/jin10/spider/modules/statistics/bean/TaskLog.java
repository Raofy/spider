package com.jin10.spider.modules.statistics.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jin10.spider.common.config.DateToLongSerializer;
import com.jin10.spider.common.utils.Constant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-26 16:25
 * ----------------------------------------------
 * <p>
 * 任务日志
 */

@Data
@Accessors(chain = true)
@Document(indexName = "task_log", type = "logs", shards = 1, replicas = 0)
public class TaskLog {


    /**
     * 任务id
     */
    @Id
    private String taskId;

    /**
     * newResultInfo : [{"title":"xxxx","url":"xxxx"}]
     * ip : 127.0.0.1
     * snapshotInfo : [{"headers":"","url":"http://www.baidu.com","content":"<html>...<\/html>","wheDetailPage":false}]
     * startTime : 1574389771000
     * remark :
     * endTime : 1574389781000
     * source : 百度新闻
     * taskId : xxx
     * spiderId : 1a1316ec101a11ea8258000c29a5948e
     * status : 0
     * filterInfo : [{"title":"xxxx","url":"xxxx"}]
     */
    @Field(type = FieldType.Keyword)
    private String spiderId;


    @Field(type = FieldType.Long)
    private Long tempId;


    /**
     * 爬虫机ip
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    private String ip;


    /**
     * 成功添加到内存的任务队列
     */
    @Field(type = FieldType.Boolean)
    private boolean puted = true;

    @Field(type = FieldType.Boolean)
    private boolean wheForeign;


    @Field(type = FieldType.Nested)
    private List<SnapshotInfoEntity> snapshotInfo;

    /**
     * 任务创建时间
     */
    @Field(type = FieldType.Keyword)
    private Date creatTime;

    /**
     * 发送给websocket服务器的时间
     */
    @Field(type = FieldType.Keyword)
    private Date sendS1Time;

    /**
     * websocket服务器接收到的时间
     */
    @Field(type = FieldType.Keyword)
    private Date receiveS1Time;

    /**
     * 推送给python端的时间
     */
    @Field(type = FieldType.Keyword)
    private Date pushTime;

    /**
     * python端开始的时间
     */
    @Field(type = FieldType.Keyword)
    private Date startTime;

    /**
     * python端结束的时间
     */
    @Field(type = FieldType.Keyword)
    private Date endTime;

    /**
     * 从mq中拉取开始处理的时间
     */
    @Field(type = FieldType.Keyword)
    private Date mqTime;

    /**
     * 改变状态的时间
     */
    @Field(type = FieldType.Keyword)
    private Date changeStateTime;

    /**
     * 整个任务终止的时间
     */
    @Field(type = FieldType.Keyword)
    private Date overTime;


    @Field(type = FieldType.Text)
    private String remark;


    @Field(type = FieldType.Text)
    private String source;


    /**
     * 0      # 系统进程结束，拿到消息但未进行爬取
     * 1      # 页面请求失败
     * 2      # 页面数据提取成功
     * 3      # 请求成功但提取不到数据
     */

    @Field(type = FieldType.Integer)
    private int status = -1;

    /**
     * (1) snapshot_hash：根据结果生成的，如果页面中提取的链接相同，则snapshot_hash也会相同
     * (2) response_code：页面返回的状态码，如果消息会展开多个链接，则取状态码最大的那个，如果爬取期间有其他异常，则该状态码为null
     */
    @Field(type = FieldType.Keyword)
    private String proxy;


    @Field(type = FieldType.Keyword)
    private String proxyPort;

    /**
     * 代理的额外字段
     */
    @Field(type = FieldType.Keyword)
    private String proxyExtra;


    /**
     * 代理类型
     */
    @Field(type = FieldType.Keyword)
    private String proxyType;


    /**
     * 完整ip地址
     */
    @Field(type = FieldType.Keyword)
    private String proxyComplete;

    /**
     * ip 代理的地区
     */
    @Field(type = FieldType.Keyword)
    private String proxyArea;

    /**
     * 是否成功
     */
    @Field(type = FieldType.Boolean)
    private boolean success;


    /**
     * 判断任务是否成功
     *
     * @return
     */
    public boolean wheSuccess() {
        if (status == Constant.TASK_LOG_STATUS.SUCCESS) {
            success = true;
            return true;
        } else {
            success = false;
            return false;
        }
    }


    @Data
    public static class SnapshotInfoEntity {
        /**
         * headers :
         * url : http://www.baidu.com
         * content : <html>...</html>
         * wheDetailPage : false
         */
        @Field(type = FieldType.Keyword)
        private DetailEntity detail;

        @Field(type = FieldType.Date)
        private Date innerStartTime;

        @Field(type = FieldType.Date)
        private Date innerEndTime;


    }


    @Data
    public static class DetailEntity {

        @Field(type = FieldType.Keyword)
        private String snapshotHash;
        @Field(type = FieldType.Integer)
        private int responseCode;
        @Field(type = FieldType.Keyword)
        private String headers;
        @Field(type = FieldType.Keyword)
        private String url;
        @Field(type = FieldType.Boolean)
        private boolean wheDetailPage;

    }


    /**
     * 任务的hash值
     */
    @Field(type = FieldType.Keyword)
    private String taskHash;


}
