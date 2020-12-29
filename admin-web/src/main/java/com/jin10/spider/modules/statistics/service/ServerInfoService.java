package com.jin10.spider.modules.statistics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.jin10.spider.common.repository.BaseEsRepository;
import com.jin10.spider.common.service.BaseEsService;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.modules.statistics.bean.ServerInfo;
import com.jin10.spider.modules.statistics.bean.ServerInfoWarn;
import com.jin10.spider.modules.statistics.handler.PushWebMsgSocketHandler;
import com.jin10.spider.modules.statistics.repository.ServerInfoRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hongda.fang
 * @date 2019-12-09 16:00
 * ----------------------------------------------
 * 爬虫服务器信息
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ServerInfoService extends BaseEsService<ServerInfo, String> {

    @Autowired
    private PushWebMsgSocketHandler pushWebMsgSocketHandler;
    @Autowired
    private ServerInfoRepository infoRepository;
    @Autowired
    private IDingTalkWarnService dingTalkWarnService;
    @Autowired
    ElasticsearchTemplate template;


    private Map<String, ServerInfoWarn> cpuWarnMap = new HashMap<>();
    private Map<String, ServerInfoWarn> memWarnMap = new HashMap<>();
    private Map<String, ServerInfoWarn> fsWarnMap = new HashMap<>();

    /**
     * 所有IP的服务器的信息
     */
    ConcurrentMap<String, ServerInfo> serverInfoMap = new ConcurrentHashMap<>();

    @Override
    protected BaseEsRepository<ServerInfo, String> getDao() {
        return infoRepository;
    }


    @Override
    public ServerInfo save(ServerInfo entity) {
        if (entity != null) {
            String public_address = entity.getIp().getPublic_address();
            serverInfoMap.put(public_address, entity);
        }
        pushWebMsgSocketHandler.transformServerInfo(entity);
        checkServerWarn(entity);
        return super.save(entity);
    }


    /**
     * 清除数据
     *
     * @param time *之前的数据
     * @return
     */
    public void deleteLog(Date time) {
        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("createTime").lte(time.getTime()));
        deleteQuery.setIndex("server_info_log");
        deleteQuery.setType("logs");
        deleteQuery.setQuery(queryBuilder);
        template.delete(deleteQuery);
    }

    public List<ServerInfo> getServerInfos() {
        return new ArrayList(serverInfoMap.values());
    }

    /**
     * 检查上报超时
     */
    public void checkTimeOutServerWarn() {
        if (CollUtil.isNotEmpty(serverInfoMap)) {
            Set<String> strings = serverInfoMap.keySet();
            for (String key : strings) {
                ServerInfo serverInfo = serverInfoMap.get(key);
                Date createTime = serverInfo.getCreateTime();
                long between = DateUtil.between(createTime, new Date(), DateUnit.MINUTE);
                if (between > 1 && between < 3) {
                    if (!serverInfo.isWarned()) {
                        ServerInfoWarn warn = new ServerInfoWarn();
                        warn.setServerInfo(serverInfo);
                        dingTalkWarnService.timeOutServerWarn(warn);
                        serverInfo.setWarned(true);
                    }
                }
            }
        }
    }


    public void checkServerWarn(ServerInfo entity) {
        ServerInfo.IpEntity ipEntity = entity.getIp();
        if (ipEntity == null) {
            return;
        }
        String ip = ipEntity.getPublic_address();
        if (StringUtils.isBlank(ip)) {
            return;
        }
        checkCpu(ip, entity);
        checkMem(ip, entity);
        checkFs(ip, entity);
    }

    private void checkCpu(String ip, ServerInfo entity) {
        ServerInfo.CpuEntity cpu = entity.getCpu();
        if (cpu != null) {
            if (cpu.getTotal() > Constant.WARN_LIMIT.CPU_MAX_TOTAL) {
                ServerInfoWarn cpuWarn = cpuWarnMap.get(ip);
                if (cpuWarn == null) {
                    cpuWarn = new ServerInfoWarn(Constant.WARN_LIMIT.CPU_KEEP_TIME, Constant.WARN_LIMIT.CPU_KEEP_TIME * 6, Constant.WARN_LIMIT.CPU, ip);
                    cpuWarnMap.put(ip, cpuWarn);
                }
                cpuWarn.addPushTimes();
                if (cpuWarn.whePush()) {
                    cpuWarn.setCpu(cpu);
                    dingTalkWarnService.serverWarn(cpuWarn);
                }
            } else {
//                cpuWarnMap.remove(ip);
            }
        }
    }

    /**
     * 检查内存是否超限
     *
     * @param ip
     * @param entity
     */
    private void checkMem(String ip, ServerInfo entity) {
        ServerInfo.MemEntity mem = entity.getMem();
        if (mem != null) {
            if (mem.getPercent() > Constant.WARN_LIMIT.MEM_MAX_TOTAL) {
                ServerInfoWarn memWarn = memWarnMap.get(ip);
                if (memWarn == null) {
                    memWarn = new ServerInfoWarn(Constant.WARN_LIMIT.MEM_KEEP_TIME, Constant.WARN_LIMIT.MEM_KEEP_TIME * 6, Constant.WARN_LIMIT.MEM, ip);
                    memWarnMap.put(ip, memWarn);
                }
                memWarn.addTimes();
                if (memWarn.whePush()) {
                    memWarn.setMem(mem);
                    dingTalkWarnService.serverWarn(memWarn);
                }
            } else {
//                memWarnMap.remove(ip);
            }
        }
    }

    private void checkFs(String ip, ServerInfo entity) {
        ServerInfo.FsEntity fs = entity.getFs();
        if (fs != null) {
            if (fs.getPercent() > Constant.WARN_LIMIT.FS_MAX_TOTAL) {
                ServerInfoWarn fsWarn = fsWarnMap.get(ip);
                if (fsWarn == null) {
                    fsWarn = new ServerInfoWarn(Constant.WARN_LIMIT.FS_KEEP_TIME, Constant.WARN_LIMIT.FS_KEEP_TIME * 6, Constant.WARN_LIMIT.FS, ip);
                    fsWarnMap.put(ip, fsWarn);
                }
                fsWarn.addTimes();
                if (fsWarn.whePush()) {
                    fsWarn.setServerInfo(entity);
                    dingTalkWarnService.serverWarn(fsWarn);
                }
            } else {
//                fsWarnMap.remove(ip);
            }
        }
    }


}
