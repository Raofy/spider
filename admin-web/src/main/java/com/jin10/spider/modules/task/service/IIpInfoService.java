package com.jin10.spider.modules.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.request.IpInfoPageRequest;
import com.jin10.spider.modules.task.request.VerifyIpRequest;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理ip 信息 服务类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-31
 */
public interface IIpInfoService extends IService<IpInfo> {


    PageUtils queryPage(IpInfoPageRequest params);

    /**
     * 根据代理等级查询待检查的代理ip
     *
     * @param proxyLevel
     * @param foreign
     * @return
     */
    List<IpInfo> findNeedCheckIpInfo(int proxyLevel, boolean foreign);


    List<IpInfo> findVailIpInfo();

    /**
     * 添加单个代理IP
     *
     * @param ipInfo
     * @return
     */
    boolean add(IpInfo ipInfo);

    IpInfo findByIp(String ip);

    /**
     * 校验Ip
     *
     * @param ip
     * @param foreign
     * @return
     */
    boolean verifyIp(String ip, boolean foreign);

    /**
     * 校验Ip, 附带权限
     *
     * @param ip
     * @param authRequest
     * @param foreign
     * @return
     */
    boolean verifyIpByAuth(String ip, VerifyIpRequest.AuthRequest authRequest, boolean foreign);




    boolean delete(Long id);

    Map<String, IpInfo> findAllMap();

    /**
     * 查询有效代理的数量
     *
     * @param proxyLevel
     * @return
     */
    Long selectCount(int proxyLevel);


}
