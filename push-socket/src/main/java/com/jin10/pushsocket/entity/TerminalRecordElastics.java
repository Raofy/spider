package com.jin10.pushsocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Airey
 * @date 2019/12/11 17:34
 * ----------------------------------------------
 * 终端消息上线下线记录
 * ----------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Document(indexName = "terminal_record", shards = 1, replicas = 0)
public class TerminalRecordElastics implements Serializable {

    private static final long serialVersionUID = 4406766713120473464L;


    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息id
     */
    @Field(type = FieldType.Keyword)
    private String msgId;

    /**
     * 用户名
     */
    @Field(type = FieldType.Keyword, analyzer = "ik_max_word")
    private String userName;

    /**
     * 用户的机器码
     */
    @Field(type = FieldType.Keyword)
    private String machineCode;


    /**
     * 爬虫内容标题
     */
    @Field(type = FieldType.Keyword, analyzer = "ik_max_word")
    private String title;

    /**
     * 爬虫内容对应的url
     */
    @Field(type = FieldType.Keyword)
    private String url;

    /**
     * 消息来源，或者网站名称
     */
    @Field(type = FieldType.Keyword)
    private String source;

    /**
     * 爬虫爬取的日期
     */
    @Field(type = FieldType.Keyword)
    private Date time;

    /**
     * 上线时间
     */
    @Field(type = FieldType.Keyword)
    private Date onlineTime;

    /**
     * 下线时间
     */
    @Field(type = FieldType.Keyword)
    private Date offlineTime;


}
