package com.jin10.spider.common.utils;

/**
 * 常量
 *
 *
 *
 * dsf
 */
public class Constant {
    /**
     * 超级管理员ID
     */
    public static final int SUPER_ADMIN = 1;
    /**
     * 数据权限过滤
     */
    public static final String SQL_FILTER = "sql_filter";
    /**
     * 当前页码
     */
    public static final String PAGE = "pageNum";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "pageSize";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     * 升序
     */
    public static final String ASC = "asc";

    /**
     * 顶级域名数量个数提醒
     */
    public static final Integer DOMAIN_NAME_COUNT = 20;

    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 定时任务状态
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public static class TEMPLATE {

        public static final int TYPE_TEXT = 1;
        public static final int TYPE_JSON = 2;
        public static final int TYPE_JSONP = 3;


        /**
         * 去重方式
         */
        public static final int FILTER_BY_LINK = 1;
        public static final int FILTER_BY_TEXT = 2;


        /**
         * 当前页面监控状态:0 停止  | 1 正在监控 | 2 过期
         */
        public static final int STATUS_STOP = 0;
        public static final int STATUS_RUNING = 1;
        public static final int STATUS_EXPIRED = 2;


        /**
         * 默认为0， 0 不需要代理 ｜ 1 随机代理 ｜ 2 低质量代理 ｜ 3 高质量代理
         */
        public static final int PROXY_LEVEL_UN_USE = 0;
        public static final int PROXY_LEVEL_RANDOM = 1;
        public static final int PROXY_LEVEL_LOW = 2;
        public static final int PROXY_LEVEL_HIGH = 3;

    }


    /**
     * 任务状态
     */
    public static class TASK_LOG_STATUS {

        /**
         * 1      # 页面请求失败
         * 2      # 页面数据提取成功
         * 3      # 请求成功但提取不到数据
         */


        public static final int SUCCESS = 2;


    }


    /**
     * 警告限制
     */
    public static class WARN_LIMIT {

        /**
         * cpu 阀值
         */
        public static final int CPU_MAX_TOTAL = 95;
        /**
         * cpu 持续时间， 单位 min
         */
        public static final int CPU_KEEP_TIME = 5;
        public static final String CPU = "CPU";

        /**
         * mem 阀值
         */
        public static final int MEM_MAX_TOTAL = 90;
        /**
         * mem 持续时间， 单位 min
         */
        public static final int MEM_KEEP_TIME = 5;
        public static final String MEM = "MEM";

        /**
         * 磁盘 阀值
         */
        public static final int FS_MAX_TOTAL = 90;
        /**
         * 磁盘 持续时间， 单位 min
         */
        public static final int FS_KEEP_TIME = 5;
        public static final String FS = "FS";


        /**
         * 代理 IP 最低数量
         */
        public static final int IP_MIN_SIZE = 1;
        public static final int IP_KEEP_TIME = 5;
        public static final String IP = "IP";


        /**
         * 任务队列警告
         */
        public static final int TASK_KEEP_TIME = 5;
        public static final String TASK = "TASK";

    }

    public static class JWT_KEY {
        /**
         * jwt 请求头
         */
        public static final String HEADER = "Authorization";

    }

}
