package com.jin10.spider.modules.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.bean.TemplateDto;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.statistics.bean.BaseWarnBean;
import com.jin10.spider.modules.statistics.dto.GroupAreaIpInfoDto;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.entity.InvadedIpInfo;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.handler.CheckIpHandler;
import com.jin10.spider.modules.task.handler.SelectIpHandler;
import com.jin10.spider.modules.task.service.IInvadedIpInfoService;
import com.jin10.spider.modules.task.service.IIpInfoService;
import com.jin10.spider.modules.task.service.IIpProxyProcessService;
import com.jin10.spider.modules.template.entity.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jin10.spider.common.utils.Constant.TEMPLATE.PROXY_LEVEL_HIGH;
import static com.jin10.spider.common.utils.Constant.TEMPLATE.PROXY_LEVEL_LOW;
import static java.util.stream.Collectors.toList;

/**
 * @author hongda.fang
 * @date 2019-12-03 14:57
 * ----------------------------------------------
 * <p>
 * ip 代理控制
 */
@Service
@Slf4j
public class IpProxyProcessServiceImpl implements IIpProxyProcessService {


    @Autowired
    private IIpInfoService ipInfoService;
    @Autowired
    private IInvadedIpInfoService invadedIpInfoService;
    @Autowired
    private CheckIpHandler checkIpHandler;
    @Autowired
    private SelectIpHandler selectIpHandler;
    @Autowired
    private IDingTalkWarnService warnService;
    @Autowired
    private RedisUtils redisUtils;

    private Map<Integer, BaseWarnBean> baseWarnBeanMap = new HashMap<>();

    /**
     * 检测ip逻辑
     *
     * @param proxyLevel 代理等级
     * @param foreign    是否国外  true 国外 false 国内
     */
    @Override
    public void checkIp(int proxyLevel, boolean foreign) {
        //1.查询需要检查的代理Ip列表
        List<IpInfo> needCheckIpInfo = ipInfoService.findNeedCheckIpInfo(proxyLevel, foreign);
        //vps不需要检测
        List<IpInfo> vpsList = needCheckIpInfo.stream().filter(item -> "vps".equalsIgnoreCase(item.getPlatform())).collect(toList());
        //其他代理需要检测
        List<IpInfo> checkIpList = needCheckIpInfo.stream().filter(item -> !"vps".equalsIgnoreCase(item.getPlatform())).collect(toList());
        //2.逐个检查代理Ip是否失效
        List<IpInfo> checkIps = checkIpHandler.checkIp(checkIpList, 5000);
        if (CollUtil.isEmpty(checkIps)) {
            checkIps = new ArrayList<>();
        }
        checkIps.addAll(vpsList);
        //3.修改ip有效性队列
        List<IpInfo> validIps = new ArrayList<>();
        //无效的ip列表
        List<IpInfo> unValidIps = new ArrayList<>();
        if (CollUtil.isNotEmpty(checkIps)) {
            //4.ip有效性修改
            selectIpHandler.changIpInfos(checkIps, proxyLevel, foreign);
            checkIps.forEach(ipInfo -> {
                if (!"vps".equalsIgnoreCase(ipInfo.getPlatform())) {
                    ipInfo.setCheckTimes(ipInfo.getCheckTimes() + 1);
                    ipInfo.setCheckTime(new Date());
                }
                if (ipInfo.isWheVaild()) {
                    validIps.add(ipInfo);
                } else {
                    unValidIps.add(ipInfo);
                }
            });

            if (CollUtil.isEmpty(validIps)) {
                //校验后有效的代理ip为空
                warnCheckIpIsNull(foreign, proxyLevel);
            } else {
                //4.更新有效代理ip信息到数据库
                List<IpInfo> validIpInfoList = validIps.stream().filter(item -> !"vps".equalsIgnoreCase(item.getPlatform())).collect(toList());
                ipInfoService.saveOrUpdateBatch(validIpInfoList);
                //5.刷新高质量，低质量代理的Zset集合
                addProxyLevelZSet(validIps, proxyLevel, foreign);
            }

            if (validIps.size() <= 5) {
                //校验后有效的代理Ip不足5个
                warnCheckIpisLeFive(foreign, proxyLevel);
            }
            if (CollUtil.isNotEmpty(unValidIps)) {
                List<Long> collect = unValidIps.stream().map(IpInfo::getId).collect(toList());
                ipInfoService.removeByIds(collect);
                List<InvadedIpInfo> invadedIpInfoList = new ArrayList<>();
                unValidIps.forEach(item -> {
                    InvadedIpInfo invadedIpInfo = new InvadedIpInfo();
                    BeanUtils.copyProperties(item, invadedIpInfo);
                    long lifeTime = DateUtil.betweenMs(item.getCreateTime(), new Date()) / 1000;
                    invadedIpInfo.setLifeTime(lifeTime);
                    invadedIpInfoList.add(invadedIpInfo);
                });
                invadedIpInfoService.saveBatch(invadedIpInfoList);
            }
        } else {
            //代理池中的ip为空
            warnUncheckIpIsNull(foreign, proxyLevel);
        }

    }


    /**
     * 更改单个Ip有效性
     *
     * @param ipInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateIpQueue(IpInfo ipInfo) {
        boolean b = ipInfoService.removeById(ipInfo.getId());
        InvadedIpInfo invadedIpInfo = new InvadedIpInfo();
        BeanUtils.copyProperties(ipInfo, invadedIpInfo);
        long lifeTime = DateUtil.betweenMs(ipInfo.getCreateTime(), new Date()) / 1000;
        invadedIpInfo.setLifeTime(lifeTime);
        boolean save = invadedIpInfoService.save(invadedIpInfo);

        selectIpHandler.changeIpInfo(ipInfo);
        String proxyIp = ipInfo.getIp();
        int proxyLevel = ipInfo.getProxyLevel();
        if (ipInfo.isWheForeign()) {
            if (proxyLevel == PROXY_LEVEL_LOW) {
                refreshProxyIpZSet(RedisKey.ACTIVEIP_ZSET_LOW_FOREIGN, proxyIp);
            } else if (proxyLevel == PROXY_LEVEL_HIGH) {
                refreshProxyIpZSet(RedisKey.ACTIVEIP_ZSET_HIGH_FOREIGN, proxyIp);
            }
        } else {
            if (proxyLevel == PROXY_LEVEL_LOW) {
                refreshProxyIpZSet(RedisKey.ACTIVEIP_ZSET_LOW, proxyIp);
            } else if (proxyLevel == PROXY_LEVEL_HIGH) {
                refreshProxyIpZSet(RedisKey.ACTIVEIP_ZSET_HIGH, proxyIp);
            }
        }

        if (save && b) {
            log.info("ip : " + ipInfo.getIp() + " 已经成功删除！！！");
        } else {
            log.info("ip : " + ipInfo.getIp() + " 删除失败！！！");
        }

    }


    /**
     * 校验后有效的代理ip为空
     *
     * @param foreign
     * @param proxyLevel
     */
    private void warnCheckIpIsNull(boolean foreign, int proxyLevel) {

        if (proxyLevel == PROXY_LEVEL_LOW) {
            String warnMsg;
            if (foreign) {
                warnMsg = "国外低质量代理Ip有效数量为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 3);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_LOW_FOREIGN);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM_FOREIGN);
            } else {
                warnMsg = "国内低质量代理Ip有效数量为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 31);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_LOW);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM);
            }
        }
        if (proxyLevel == PROXY_LEVEL_HIGH) {
            String warnMsg;
            if (foreign) {
                warnMsg = "国外高质量代理Ip有效数量为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 4);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_HIGH_FOREIGN);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM_FOREIGN);
            } else {
                warnMsg = "国内高质量代理Ip有效数量为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 41);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_HIGH);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM);
            }

        }

    }


    /**
     * 校验后有效的代理Ip不足5个
     *
     * @param foreign
     * @param proxyLevel
     */
    private void warnCheckIpisLeFive(boolean foreign, int proxyLevel) {

        if (proxyLevel == PROXY_LEVEL_LOW) {
            if (foreign) {
                String warnMsg = "国外低质量代理Ip数量不足5个,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 5);
            } else {
                String warnMsg = "国内低质量代理Ip数量不足5个,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 51);
            }

        }
        if (proxyLevel == PROXY_LEVEL_HIGH) {
            if (foreign) {
                String warnMsg = "国外高质量代理Ip数量不足5个,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 6);
            } else {
                String warnMsg = "国内高质量代理Ip数量不足5个,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 61);
            }

        }

    }

    /**
     * 代理池中的ip为空
     *
     * @param foreign
     * @param proxyLevel
     */
    private void warnUncheckIpIsNull(boolean foreign, int proxyLevel) {
        if (proxyLevel == PROXY_LEVEL_LOW) {
            if (foreign) {
                String warnMsg = "国外代理池中低质量代理ip为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 1);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_LOW_FOREIGN);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM_FOREIGN);
            } else {
                String warnMsg = "国内代理池中低质量代理ip为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 11);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_LOW);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM);
            }

        }
        if (proxyLevel == PROXY_LEVEL_HIGH) {
            if (foreign) {
                String warnMsg = "国外代理池中有效的高质量代理ip为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 2);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_HIGH_FOREIGN);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM_FOREIGN);
            } else {
                String warnMsg = "国内代理池中有效的高质量代理ip为NULL,请检查代理ip池是否正常！！！";
                log.error(warnMsg);
                ipWarn(warnMsg, 21);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_HIGH);
                redisUtils.del(RedisKey.ACTIVEIP_ZSET_RANDOM);
            }

        }
    }


    /**
     * 刷新高质量，低质量代理的Zset集合
     *
     * @param validIps
     */
    private void addProxyLevelZSet(List<IpInfo> validIps, int proxyLevel, boolean foreign) {

        if (foreign) {
            if (proxyLevel == PROXY_LEVEL_LOW) {
                refreshProxyIpZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_LOW_FOREIGN, validIps);
            } else if (proxyLevel == PROXY_LEVEL_HIGH) {
                refreshProxyIpZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_HIGH_FOREIGN, validIps);
            }
        } else {
            if (proxyLevel == PROXY_LEVEL_LOW) {
                refreshProxyIpZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_LOW, validIps);
            } else if (proxyLevel == PROXY_LEVEL_HIGH) {
                refreshProxyIpZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_HIGH, validIps);
            }
        }

    }


    /**
     * 刷新对应条件的代理ip集合
     *
     * @param proxyLevel
     * @param redisKey
     */
    private void refreshProxyIpZSet(int proxyLevel, String redisKey, List<IpInfo> validIps) {

        Set<Object> proxyIpZSet = redisUtils.zRange(redisKey, 0, -1);
        Set<String> proxyIpInfoList = validIps.stream().filter(item -> item.getProxyLevel() == proxyLevel).map(IpInfo::getIp).collect(Collectors.toSet());
        List<Object> removeList = proxyIpZSet.stream().filter(item -> !proxyIpInfoList.contains(item)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(removeList)) {
            log.info("从代理队列 {" + redisKey + "} 中删除失效的ip列表为 : " + removeList);
            redisUtils.zRemove(redisKey, removeList.toArray());
        }
        Set<String> addList = proxyIpInfoList.stream().filter(item -> !proxyIpZSet.contains(item)).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(addList)) {
            Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = initTypedTuple(addList);
            redisUtils.zAdd(redisKey, typedTupleSet);
        }

    }

    /**
     * 刷新对应条件的代理Ip集合
     *
     * @param redisKey
     * @param proxyIp
     */
    private void refreshProxyIpZSet(String redisKey, String proxyIp) {
        log.info("单个删除！！！   从代理队列 {" + redisKey + "} 中删除失效的ip列表为 : " + proxyIp);
        redisUtils.zRemove(redisKey, proxyIp);
    }


    /**
     * 将set转换为TypedTuple类型
     *
     * @param levelSet
     * @return
     */
    private Set<ZSetOperations.TypedTuple<Object>> initTypedTuple(Set<String> levelSet) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = new HashSet<>();
        levelSet.forEach(item -> {
            ZSetOperations.TypedTuple typedTuple = new DefaultTypedTuple(item, 100d);
            typedTuples.add(typedTuple);
        });
        return typedTuples;
    }


    /**
     * 寻找合适的代理ip
     *
     * @param taskDtos
     */
    @Override
    public void takeProxyIp(List<UrlTaskDto> taskDtos) {
        List<UrlTaskDto> filterUrlTaskList = taskDtos.stream().filter(item -> item.getTemp().getProxyLevel() > 0).collect(toList());
        if (CollUtil.isEmpty(filterUrlTaskList)) {
            return;
        }
        for (UrlTaskDto dto : filterUrlTaskList) {
            TemplateDto templateDto = dto.getTemp();
            if (templateDto == null) {
                continue;
            }
            String selectIp = selectIpHandler.selectProxyIp(templateDto.getDomainName());
            if (StringUtils.isNotBlank(selectIp)) {
                setProxyInfo(templateDto, selectIp);
            } else {
                log.warn("根据模板id分配ip失败,开始为模板" + dto.getTemp() + "分配随机Ip");
                int proxyLevel = templateDto.getProxyLevel();
                String ip = findProxyIp(proxyLevel, dto.isWeForeign());
                if (StringUtils.isNotBlank(ip)) {
                    setProxyInfo(templateDto, selectIp);
                } else {
                    taskDtos.remove(dto);
                    Template template = (Template) redisUtils.get(RedisKey.TEMPLATE_SUFFIX + dto.getTemp().getTempId());
                    template.getConcurrentNum().decrementAndGet();
                    template.getTaskUuidMap().remove(dto.getTaskUuid());
                    redisUtils.set(RedisKey.TEMPLATE_SUFFIX + dto.getTemp().getTempId(), template);
                    redisUtils.del(RedisKey.URLTASK_PREFIX + dto.getTaskUuid());
                    String msg = "domainName = " + dto.getTemp().getDomainName() + ",tempId = " + dto.getTemp().getTempId() + "没有找到合适的代理ip！！！请检查代理ip池是否正常！！！";
                    ipWarn(msg, 7);
                    log.error(msg);
                }
            }

        }

    }


    /**
     * 设置代理信息
     *
     * @param templateDto
     * @param selectIp
     */
    public void setProxyInfo(TemplateDto templateDto, String selectIp) {
        templateDto.setProxy(selectIp);
        IpInfo ipInfo = (IpInfo) redisUtils.hget(RedisKey.VAILDIP_TOTAL_MAP, selectIp);
        if (ObjectUtil.isNotNull(ipInfo)) {
            templateDto.setProxyExtra(ipInfo.getExtra());
            templateDto.setProxyType(ipInfo.getType());
            templateDto.setProxyWheForeign(ipInfo.isWheForeign());
        }
    }


    /**
     * 按地区分组
     *
     * @param validIps
     * @return
     */
    @Override
    public List<GroupAreaIpInfoDto> groupAreaIpInfo(List<IpInfo> validIps) {
        List<GroupAreaIpInfoDto> ipInfoDtos = new ArrayList<>();
        validIps.forEach(ipInfo -> {
            if (StringUtils.isEmpty(ipInfo.getArea())) {
                ipInfo.setArea("中国");
            }
        });
        Map<String, List<IpInfo>> collect = validIps.stream().collect(Collectors.groupingBy(IpInfo::getArea));
        for (String key : collect.keySet()) {
            GroupAreaIpInfoDto ipInfoDto = new GroupAreaIpInfoDto();
            ipInfoDto.setArea(key);
            ipInfoDto.setIpInfos(collect.get(key));
            ipInfoDtos.add(ipInfoDto);
        }
        return ipInfoDtos;
    }


    /**
     * 获取合适的代理Ip
     *
     * @param proxyLevel
     * @return
     */
    private String findProxyIp(int proxyLevel, boolean foreign) {
        String proxyIp;
        if (foreign) {
            proxyIp = findProxyIpByZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_LOW_FOREIGN, RedisKey.ACTIVEIP_ZSET_HIGH_FOREIGN, RedisKey.ACTIVEIP_ZSET_RANDOM_FOREIGN);
        } else {
            proxyIp = findProxyIpByZSet(proxyLevel, RedisKey.ACTIVEIP_ZSET_LOW, RedisKey.ACTIVEIP_ZSET_HIGH, RedisKey.ACTIVEIP_ZSET_RANDOM);
        }
        return proxyIp;
    }


    /**
     * 从有序集合中获取代理ip
     *
     * @param proxyLevel
     * @param lowRedisKey
     * @param highRedisKey
     * @param randomRedisKey
     * @return
     */
    private String findProxyIpByZSet(int proxyLevel, String lowRedisKey, String highRedisKey, String randomRedisKey) {

        Long lowSize = redisUtils.zZCard(lowRedisKey);
        Long highSize = redisUtils.zZCard(highRedisKey);
        String ip = "";
        if (proxyLevel == Constant.TEMPLATE.PROXY_LEVEL_HIGH && highSize > 0) {
            Set<Object> highSet = redisUtils.zRange(highRedisKey, 0, 0);
            if (CollUtil.isNotEmpty(highSet)) {
                ip = (String) highSet.iterator().next();
                redisUtils.zAdd(highRedisKey, ip, System.currentTimeMillis());
            }
        } else if (proxyLevel == PROXY_LEVEL_LOW && lowSize > 0) {
            Set<Object> lowSet = redisUtils.zRange(lowRedisKey, 0, 0);
            if (CollUtil.isNotEmpty(lowSet)) {
                ip = (String) lowSet.iterator().next();
                redisUtils.zAdd(lowRedisKey, ip, System.currentTimeMillis());
            }
        } else {
            redisUtils.del(randomRedisKey);
            Set<Object> lowAllSet = redisUtils.zRange(lowRedisKey, 0, -1);
            Set<Object> highAllSet = redisUtils.zRange(highRedisKey, 0, -1);
            redisUtils.zUnionAndStore(highRedisKey, lowRedisKey, randomRedisKey);
            Set<Object> randomSet = redisUtils.zRange(randomRedisKey, 0, 0);
            if (CollUtil.isNotEmpty(randomSet)) {
                ip = (String) randomSet.iterator().next();
                if (highAllSet.contains(ip)) {
                    redisUtils.zAdd(highRedisKey, ip, System.currentTimeMillis());
                } else if (lowAllSet.contains(ip)) {
                    redisUtils.zAdd(lowRedisKey, ip, System.currentTimeMillis());
                }
            }
        }
        return ip;
    }

    /**
     * ip代理池警告
     *
     * @param warnMsg
     * @param flag
     */
    private void ipWarn(String warnMsg, int flag) {
        BaseWarnBean baseWarnBean = baseWarnBeanMap.get(flag);
        if (ObjectUtil.isNull(baseWarnBean)) {
            baseWarnBean = new BaseWarnBean(Constant.WARN_LIMIT.IP_KEEP_TIME, Constant.WARN_LIMIT.IP);
            warnService.msgWarn(warnMsg);
            baseWarnBeanMap.put(flag, baseWarnBean);
        } else {
            if (baseWarnBean.whePush()) {
                warnService.msgWarn(warnMsg);
            }
            baseWarnBeanMap.put(flag, baseWarnBean);
        }
    }


}


