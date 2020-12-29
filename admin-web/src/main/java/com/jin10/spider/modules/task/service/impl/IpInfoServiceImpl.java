package com.jin10.spider.modules.task.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.exception.InvalidIpException;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.common.utils.QueryPage;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.handler.CheckIpHandler;
import com.jin10.spider.modules.task.handler.SelectIpHandler;
import com.jin10.spider.modules.task.mapper.IpInfoMapper;
import com.jin10.spider.modules.task.request.IpInfoPageRequest;
import com.jin10.spider.modules.task.request.VerifyIpRequest;
import com.jin10.spider.modules.task.service.IIpInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 代理ip 信息 服务实现类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-31
 */
@Service
public class IpInfoServiceImpl extends ServiceImpl<IpInfoMapper, IpInfo> implements IIpInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private SelectIpHandler ipHandler;


    @Autowired
    private CheckIpHandler checkIpHandler;

    @Override
    public PageUtils queryPage(IpInfoPageRequest params) {

        LambdaQueryWrapper<IpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(IpInfo::getCreateTime);

        if (StringUtils.isNotBlank(params.getPlatform())) {
            queryWrapper.eq(IpInfo::getPlatform, params.getPlatform());
        }

        if (ObjectUtil.isNotNull(params.getProxyLevel())) {
            queryWrapper.eq(IpInfo::getProxyLevel, params.getProxyLevel());
        }

        if (StringUtils.isNotBlank(params.getType())) {
            queryWrapper.eq(IpInfo::getType, params.getType());
        }

        IPage<IpInfo> page = this.page(new QueryPage<IpInfo>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<IpInfo> findNeedCheckIpInfo(int proxyLevel, boolean foreign) {
        LambdaQueryWrapper<IpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IpInfo::isWheVaild, true);
        queryWrapper.eq(IpInfo::getProxyLevel, proxyLevel);
        queryWrapper.eq(IpInfo::isWheForeign, foreign);
        List<IpInfo> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<IpInfo> findVailIpInfo() {
        return list();
    }

    /**
     * 添加单个代理IP
     *
     * @param ipInfo
     * @return
     */
    @Override
    public boolean add(IpInfo ipInfo) {

        String ip = ipInfo.getIp();
        boolean isExist = redisUtils.hHasKey(RedisKey.VAILDIP_TOTAL_MAP, ip);
        if (isExist) {
            throw new InvalidIpException(ip + "已存在");
        }
        boolean isSuccess = false;
        JSONObject extra;
        if (JSONObject.isValidObject(ipInfo.getExtra())) {
            extra = JSONObject.parseObject(ipInfo.getExtra());
        } else {
            throw new InvalidIpException("extra 参数错误！！！");
        }

        if (extra.size() == 0) {
            isSuccess = verifyIp(ip, ipInfo.isWheForeign());
        } else {
            if (extra.containsKey("auth")) {
                VerifyIpRequest.AuthRequest authRequest = extra.getObject("auth", VerifyIpRequest.AuthRequest.class);
                isSuccess = verifyIpByAuth(ip, authRequest, ipInfo.isWheForeign());
            }
        }
        if (isSuccess) {
            ipInfo.setWheVaild(true);
            ipInfo.setCheckTime(new Date());
            ipInfo.setCheckTimes(1);
            save(ipInfo);
        } else {
            throw new InvalidIpException(ip + "为无效IP");
        }
        return true;
    }


    @Override
    public IpInfo findByIp(String ip) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ip", ip);
        queryWrapper.last(" limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 校验ip
     *
     * @param ip
     * @return
     */
    @Override
    public boolean verifyIp(String ip, boolean foreign) {
        return checkIpHandler.ipIsValid(ip, 5000, foreign);
    }


    /**
     * 校验Ip, 附带权限
     *
     * @param ip
     * @param authRequest
     * @return
     */
    @Override
    public boolean verifyIpByAuth(String ip, VerifyIpRequest.AuthRequest authRequest, boolean foreign) {
        return checkIpHandler.ipIsVaild(ip, authRequest, 5000, foreign);
    }


    @Override
    public boolean delete(Long id) {
        IpInfo ipInfo = baseMapper.selectById(id);
        if (ipInfo != null) {
            boolean b = removeById(id);
            ipHandler.removeIp(ipInfo.getIp());
            return b;
        }
        return false;
    }

    @Override
    public Map<String, IpInfo> findAllMap() {
        List<IpInfo> ipInfos = baseMapper.groupByIp();
        Map<String, IpInfo> collect = ipInfos.stream().collect(Collectors.toMap(IpInfo::getIp, Function.identity(), (key1, key2) -> key2));
        return collect;
    }

    /**
     * 查询有效代理的数量
     *
     * @param proxyLevel
     * @return
     */
    @Override
    public Long selectCount(int proxyLevel) {
        return baseMapper.selectCount(proxyLevel);
    }


}
