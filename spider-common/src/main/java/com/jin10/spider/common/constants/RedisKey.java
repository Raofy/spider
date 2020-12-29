package com.jin10.spider.common.constants;

/**
 * @author Airey
 * @date 2020/1/15 14:49
 * ----------------------------------------------
 * redis中的key值常量类
 * ----------------------------------------------
 */
public interface RedisKey {

    /**
     * tempId集合
     */
    String TEMPIDMAP = "tempIdMap";

    /**
     * 顶级域名对应的数量集合
     */
    String DOMAIN_NAME_MAP = "domainNameMap";


    /**
     * 需要代理的顶级域名集合 顶级域名:代理等级
     */
    String DOMAIN_PROXY_MAP = "domainProxyMap";


    /**
     * 代理Ip对应的域名详情
     */
    String PROXTIP_DOMAIN_MAP = "proxyIp_domain_Map";

    /**
     * 有效的代理Ip 高质量
     */
    String VAILDIP_HIGH_MAP = "vaildIpHighMap";

    /**
     * 有效的代理Ip 低质量
     */
    String VAILDIP_LOW_MAP = "vaildIpLowMap";

    /**
     * 有效的代理Ip 国外高质量
     */
    String VALIDIP_HIGN_FOREIGN_MAP = "validIpHighForeignMap";

    /**
     * 有效的代理Ip 国外低质量
     */
    String VALIDIP_LOW_FOREIGN_MAP = "validIpLowForeignMap";


    /**
     * 所有有效的代理IP
     */
    String VAILDIP_TOTAL_MAP = "vaildIpTotalMap";


    /**
     * 实时模板列表
     */
    String TEMPLATE_SUFFIX = "template:";


    /**
     * urlTask对象
     */
    String URLTASK_PREFIX = "urlTask:";


    /**
     * 有效的ip优先级列表 低质量代理
     */
    String ACTIVEIP_ZSET_LOW = "activeIpZset:Low";


    /**
     * 有效的ip优先级列表 高质量代理
     */
    String ACTIVEIP_ZSET_HIGH = "activeIpZset:High";


    /**
     * 有效的ip优先级列表 随机代理
     */
    String ACTIVEIP_ZSET_RANDOM = "activeIpZset:Random";


    /**
     * 有效的ip优先级列表 国外低质量
     */
    String ACTIVEIP_ZSET_LOW_FOREIGN = "activeIpZset:ForeignLow";


    /**
     * 有效的ip优先级列表 国外高质量
     */
    String ACTIVEIP_ZSET_HIGH_FOREIGN = "activeIpZset:ForeignHigh";


    /**
     * 有效的ip优先级列表 随机代理 国外
     */
    String ACTIVEIP_ZSET_RANDOM_FOREIGN = "activeIpZset:ForeignRandom";


    /**
     * 顶级域名优先级队列
     */
    String DOMAIN_NAME_ZSET = "domainNameZset:";


    /**
     * 维护人员对应维护的模板列表
     */
    String MAINTAINER_MAP = "maintainerMap";

    /**
     * 通过hash码去重 单个去重
     */
    String HASH_CODE_TITLE = "hashCodeTitle:";

    /**
     * 通过hash码去重 批量去重
     */
    String HASH_CODE_BULK = "hashCodeBulk:";

    /**
     * 国内爬虫节点调度
     */
    String SPIDER_NODE_ZSET="spiderNodeZset:home";

    /**
     * 国外爬虫节点调度
     */
    String SPIDER_NODE_FOREIGN_ZSET="spiderNodeZset:foreign";


}
