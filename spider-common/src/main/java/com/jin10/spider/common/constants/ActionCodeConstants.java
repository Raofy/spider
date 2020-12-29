package com.jin10.spider.common.constants;

/**
 * @author Airey
 * @date 2019/12/11 15:07
 * ----------------------------------------------
 * socket消息状态码定义类
 * ----------------------------------------------
 */
public interface ActionCodeConstants {
    //  ================================================  spider-server
    /**
     * 客户端发送补全最新消息请求
     */
    int COMPLETE_MSG = 301;

    /**
     * 客户端发送筛选消息请求
     */
    int SCREEN_MSG = 360;

    /**
     * 筛选消息接收分组
     */
    int SCREEN_MSG_GROUP = 361;


    /**
     * 客户端发送推送消息请求
     */
    int PUSH_MSG = 370;

    /**
     * 推送消息接收分组
     */
    int PUSH_MSG_GROUP = 371;


    /**
     * 客户端发送终端消息消费记录信息请求
     */
    int TERMINAL_RECORD = 380;


    /**
     * 客户端发送快讯消息
     */
    int FLASH_MSG = 395;


    /**
     * 正常转发爬虫消息分组
     */
    int NORMAL_MSG_GROUP = 702;


    /**
     * 服务端心跳响应
     */
    int HEART_BEAT = 998;


//    ======================================= spider-admin

    /**
     * 爬虫获取任务
     */
    int GET_TASK = 1;

    /**
     * 爬虫服务器上下线
     */
    int SOCKET_ACTIVE = 100;

    /**
     * 监控获取 爬虫服务器 信息
     */
    int GET_SERVER_INFOS = 1001;

    /**
     * 监控获取 任务队列信息
     */
    int TASK_QUEUE_INFO = 1011;

    /**
     * 获取 IP 代理信息
     */
    int GET_PROXY_IP = 1021;


    /**
     * 代理IP使用
     */
    int GET_PROXY_IP_USE = 1031;

//  =============================================== spider-socket

    /**
     * 转发爬虫调度端spider-admin的消息
     */
    int TRASFORM_MSG = 720;

    /**
     * 爬虫客户端请求接收推送消息并加入国外分组中
     */
    int TASK_PUSH_FOREIGN = 711;


    /**
     * 爬虫客户端请求接收推送消息并加入分组
     */
    int TASK_PUSH = 721;


    /**
     * 客户端返回状态消息(国外)
     */
    int CLIENT_FOREIGN_STATUS = 713;


    /**
     * 客户端返回状态消息
     */
    int CLIENT_STATUS = 723;


    /**
     * python端返回的任务日志消息
     */
    int TASK_LOG_MSG = 871;

    /**
     * ping 消息
     */
    int PYTHON_PING_MSG = 666;

    /**
     * pong 消息
     */
    int PYTHON_PONG_MSG = 667;
}
