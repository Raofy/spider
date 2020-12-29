package com.jin10.spider.modules.task.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.task.bean.ProxyTemplate;
import com.jin10.spider.modules.task.entity.IpInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hongda.fang
 * @date 2019-12-05 17:54
 * ----------------------------------------------
 * 选择IP
 */
@Component
@Slf4j
public class SelectIpHandler {


    @Autowired
    private RedisUtils redisUtils;


    /**
     * ip有效性修改，随后更改 队列
     *
     * @param checkIpInfos
     */
    public void changIpInfos(List<IpInfo> checkIpInfos, int proxyLevel, boolean foreign) {

        if (foreign) {
            if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_HIGH) {
                validIpByProxyLevel(checkIpInfos, RedisKey.VALIDIP_HIGN_FOREIGN_MAP);
            } else if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_LOW) {
                validIpByProxyLevel(checkIpInfos, RedisKey.VALIDIP_LOW_FOREIGN_MAP);
            }
        } else {
            if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_HIGH) {
                validIpByProxyLevel(checkIpInfos, RedisKey.VAILDIP_HIGH_MAP);
            } else if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_LOW) {
                validIpByProxyLevel(checkIpInfos, RedisKey.VAILDIP_LOW_MAP);
            }
        }

    }


    /**
     * 移除无效ip
     *
     * @param ipInfo
     */
    public void changeIpInfo(IpInfo ipInfo) {

        int proxyLevel = ipInfo.getProxyLevel();
        String proxyIp = ipInfo.getIp();
        removeIp(proxyIp);
        if (ipInfo.isWheForeign()) {
            if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_HIGH) {
                redisUtils.hdel(RedisKey.VALIDIP_HIGN_FOREIGN_MAP, proxyIp);
            } else if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_LOW) {
                redisUtils.hdel(RedisKey.VALIDIP_LOW_FOREIGN_MAP, proxyIp);
            }
        } else {
            if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_HIGH) {
                redisUtils.hdel(RedisKey.VAILDIP_HIGH_MAP, proxyIp);
            } else if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_LOW) {
                redisUtils.hdel(RedisKey.VAILDIP_LOW_MAP, proxyIp);
            }
        }
        redisUtils.hdel(RedisKey.VAILDIP_TOTAL_MAP, proxyIp);

    }


    /**
     * 通过代理等级校验Ip
     *
     * @param checkIpInfos
     * @param redisKey
     */
    private void validIpByProxyLevel(List<IpInfo> checkIpInfos, String redisKey) {
        //有效的ip
        List<IpInfo> curValidIps = checkIpInfos.stream().filter(IpInfo::isWheVaild).collect(Collectors.toList());
        //有效的ip Map
        Map<@NotNull String, IpInfo> validMap = curValidIps.stream().collect(Collectors.toMap(IpInfo::getIp, v -> v, (v1, v2) -> v1));
        //有效的ip Set
        Set<@NotNull String> curValidSet = curValidIps.stream().map(IpInfo::getIp).collect(Collectors.toSet());

        //Redis中有效的ip Map
        Map<Object, Object> validIpRedisMap = redisUtils.hmget(redisKey);
        //Redis中有效的ip Set
        Set<Object> oldValidSet = validIpRedisMap.keySet();

        /**
         * 差集 (curValidIps - oldValidSet) = 需要新增的IP  Redis有效ip集合中不包含的当前有效ip
         */
        Set<IpInfo> addIps = curValidIps.stream().filter(item -> !oldValidSet.contains(item.getIp())).collect(Collectors.toSet());
        /**
         * 差集 (oldValidSet - curValidIps ) = 需要删除的无效IP 当前有效的ip集合中不包含的Redis的有效ip
         */
        Set<Object> removeIpObj = oldValidSet.stream().filter(ip -> !curValidSet.contains(ip)).collect(Collectors.toSet());
        Set<String> removeIps = removeIpObj.stream().map(Object::toString).collect(Collectors.toSet());

        if (CollUtil.isNotEmpty(addIps)) {
            addIps.forEach(ipInfo -> redisUtils.hset(RedisKey.VAILDIP_TOTAL_MAP, ipInfo.getIp(), ipInfo));
            addIps.forEach(this::addDomainNameZSet);
        }

        if (CollUtil.isNotEmpty(removeIps)) {
            removeIps.forEach(ip -> redisUtils.hdel(RedisKey.VAILDIP_TOTAL_MAP, ip));
            removeIps.forEach(this::removeIp);
        }

        if (CollUtil.isNotEmpty(validMap)) {
            redisUtils.del(redisKey);
            validMap.forEach((k, v) -> redisUtils.hset(redisKey, k, v));
        }


    }


    /**
     * 添加到顶级域名队列中
     *
     * @param ipInfo
     */
    private void addDomainNameZSet(IpInfo ipInfo) {

        Map<Object, Object> domainProxyMap = redisUtils.hmget(RedisKey.DOMAIN_PROXY_MAP);
        Object proxyIpDomainSet = redisUtils.hget(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp());
        if (CollUtil.isEmpty(domainProxyMap)) {
            log.warn("警告！！！需要代理的顶级域名为NULL！");
            return;
        }
        Set<String> domainSet = new HashSet<>();
        for (Map.Entry<Object, Object> entry : domainProxyMap.entrySet()) {
            String domainName = entry.getKey().toString();
            ProxyTemplate proxyTemplate = (ProxyTemplate) entry.getValue();
            if (canAdd(proxyTemplate, ipInfo)) {
                redisUtils.zAdd(RedisKey.DOMAIN_NAME_ZSET + domainName, ipInfo.getIp(), System.currentTimeMillis());
                domainSet.add(domainName);
            }
            if (CollUtil.isEmpty(domainSet)) {
                continue;
            }
            if (ObjectUtil.isNull(proxyIpDomainSet)) {
                redisUtils.hset(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp(), domainSet);
            } else if (ObjectUtil.isNotNull(proxyIpDomainSet) && proxyIpDomainSet instanceof Set) {
                Set<String> proxyIpDomainSetCopy = (Set<String>) proxyIpDomainSet;
                proxyIpDomainSetCopy.addAll(domainSet);
                redisUtils.hset(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp(), proxyIpDomainSetCopy);
            }
        }

    }


//    /**
//     * 判断是否能够添加
//     *
//     * @param proxyLevel
//     * @param ipInfo
//     * @return
//     */
//    private boolean canAdd(Integer proxyLevel, IpInfo ipInfo) {
//        if (proxyLevel > 0) {
//            if (Constant.TEMPLATE.PROXY_LEVEL_RANDOM == proxyLevel) {
//                return true;
//            }
//            return proxyLevel == ipInfo.getProxyLevel();
//        }
//        return false;
//    }


    /**
     * 判断是否能够添加
     *
     * @param proxyTemplate
     * @param ipInfo
     * @return
     */
    private boolean canAdd(ProxyTemplate proxyTemplate, IpInfo ipInfo) {

        if (proxyTemplate.getWheForeign().equals(ipInfo.isWheForeign())) {
            if (proxyTemplate.getProxyLevel() > 0) {
                if (proxyTemplate.getProxyLevel().equals(Constant.TEMPLATE.PROXY_LEVEL_RANDOM)) {
                    return true;
                }
                return proxyTemplate.getProxyLevel().equals(ipInfo.getProxyLevel());
            }
        }
        return false;
    }


    /**
     * 移除代理ip
     *
     * @param ip
     */
    public void removeIp(String ip) {
        Object domainSet = redisUtils.hget(RedisKey.PROXTIP_DOMAIN_MAP, ip);
        if (ObjectUtil.isNotNull(domainSet)) {
            if (domainSet instanceof Set) {
                Set<String> domainSetNew = (Set<String>) domainSet;
                domainSetNew.forEach(item -> redisUtils.zRemove(RedisKey.DOMAIN_NAME_ZSET + item, ip));
            }
        }
        redisUtils.hdel(RedisKey.PROXTIP_DOMAIN_MAP, ip);
    }


    /**
     * 新增顶级域名到顶级域名队列，并重新分配ip
     *
     * @param domainName
     * @param newProxyMap
     */
    public void addDomain(String domainName, Map<String, ProxyTemplate> newProxyMap) {

        ProxyTemplate proxyTemplate = MapUtil.get(newProxyMap, domainName, ProxyTemplate.class);
        /**
         * 所有有效的代理IP
         */
        Map<Object, Object> validIpRedisMap = redisUtils.hmget(RedisKey.VAILDIP_TOTAL_MAP);
        Set<String> domainSet = new HashSet<>();
        if (CollUtil.isNotEmpty(validIpRedisMap)) {
            validIpRedisMap.forEach((k, v) -> {
                IpInfo ipInfo = (IpInfo) v;
                if (canAdd(proxyTemplate, ipInfo)) {
                    redisUtils.zAdd(RedisKey.DOMAIN_NAME_ZSET + domainName, ipInfo.getIp(), System.currentTimeMillis());
                    Object domainSetR = redisUtils.hget(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp());
                    if (ObjectUtil.isNull(domainSetR)) {
                        domainSet.add(domainName);
                        redisUtils.hset(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp(), domainSet);
                    } else {
                        Set<String> domainSetRCopy = (Set<String>) domainSetR;
                        domainSetRCopy.add(domainName);
                        redisUtils.hset(RedisKey.PROXTIP_DOMAIN_MAP, ipInfo.getIp(), domainSetRCopy);
                    }
                }
            });
        }


    }


    /**
     * 刷新代理模板
     *
     * @param newProxyDomainMap
     */
    public void refreshProxyDomainName(Map<String, ProxyTemplate> newProxyDomainMap) {

        if (CollUtil.isNotEmpty(newProxyDomainMap)) {
            Set<String> newDomainSet = newProxyDomainMap.keySet();
            Map<Object, Object> domainNameMap = redisUtils.hmget(RedisKey.DOMAIN_PROXY_MAP);
            Set<Object> oldDomainSet = domainNameMap.keySet();
            /**
             * 差集 (newDomainSet-oldDomainSet) = 需要新增的顶级域名
             */
            Set<String> addDomains = newDomainSet.stream().filter(item -> !oldDomainSet.contains(item)).collect(Collectors.toSet());
            /**
             * 差集 (oldDomainSet-newDomainSet) = 需要删除的顶级域名
             */
            Set<Object> removeDomains = oldDomainSet.stream().filter(item -> !newDomainSet.contains(item)).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(removeDomains)) {
                removeDomains.forEach(item -> redisUtils.del(RedisKey.DOMAIN_NAME_ZSET + item));
            }
            if (CollUtil.isNotEmpty(addDomains)) {
                addDomains.forEach(item -> addDomain(item, newProxyDomainMap));
            }
            redisUtils.del(RedisKey.DOMAIN_PROXY_MAP);
            newProxyDomainMap.forEach((k, v) -> redisUtils.hset(RedisKey.DOMAIN_PROXY_MAP, k, v));
        }

    }


    /**
     * 根据顶级域名选择代理IP
     *
     * @param domainName
     * @return
     */
    public String selectProxyIp(String domainName) {
        String ip = "";
        Set<Object> domainIpSet = redisUtils.zRange(RedisKey.DOMAIN_NAME_ZSET + domainName, 0, 0);
        if (CollUtil.isNotEmpty(domainIpSet)) {
            ip = (String) domainIpSet.iterator().next();
            redisUtils.zAdd(RedisKey.DOMAIN_NAME_ZSET + domainName, ip, System.currentTimeMillis());
        }
        return ip;
    }


}
