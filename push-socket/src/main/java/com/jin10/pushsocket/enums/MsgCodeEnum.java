package com.jin10.pushsocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Airey
 * @date 2019/11/22 15:24
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Getter
@AllArgsConstructor
public enum MsgCodeEnum {


    COMPLETE(302, "消息补全"),

    LATEST(602, "获取最新的消息"),

    NORMAL(702, "正常转发爬虫消息"),

    SCREEN(361, "筛选消息"),

    PUSH(371, "推送消息"),

    HEART(999, "心跳消息"),


    SERVER_INFOS(1002, "爬虫服务器信息list"),
    SERVER_INFO(1003, "单个爬虫服务器信息"),
    TASK_QUEUE(1012, "队列任务情况"),
    TASK_QUEUE_PRODUCT(1013, "新增任务"),
    PROXY_IP_LIST(1022, "IP代理地区分组list"),
    PROXY_IP_USE(1032, "IP代理 使用"),


    TASK_LOG_RETURN(872, "实时爬虫日志消息"),

    ;


    private int code;

    private String message;

}
