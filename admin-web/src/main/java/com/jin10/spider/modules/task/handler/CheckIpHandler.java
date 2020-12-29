package com.jin10.spider.modules.task.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.config.CustomConfig;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.request.VerifyIpRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author hongda.fang
 * @date 2019-10-31 10:03
 * ----------------------------------------------
 * 获取ip
 */
@Component
public class CheckIpHandler {

    @Autowired
    CustomConfig customConfig;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public int timeOut = 10000;

    private final String ipSplit = ":";

    /**
     * 检查ip有效性
     *
     * @param timeout 超时时长
     */
    private IpInfo checkIpValid(IpInfo ipInfo, int timeout) {
        int status = 0;
        try {
            String ip = ipInfo.getIp();
            if (StringUtils.isNotBlank(ip) && ip.contains(ipSplit)) {
                ip = ip.trim();
                ipInfo.setDelay(null);
                String[] split = ip.split(ipSplit);
                if (split.length == 2) {
//                    logger.info("request proxy ip is " + ip);
                    long curTime = System.currentTimeMillis();
                    boolean isMain = ipInfo.isWheForeign();
                    JSONObject extra = new JSONObject();
                    if (JSONObject.isValidObject(ipInfo.getExtra())) {
                        extra = JSONObject.parseObject(ipInfo.getExtra());
                    }
                    if (extra.size() == 0) {
                        status = request(ip, timeout, isMain);
                    } else {
                        if (extra.containsKey("auth")) {
                            VerifyIpRequest.AuthRequest authRequest = extra.getObject("auth", VerifyIpRequest.AuthRequest.class);
                            status = request(ip, authRequest, timeout, isMain);
                        }
                    }
                    ipInfo.setDelay(System.currentTimeMillis() - curTime);
                }
            }
        } catch (Exception ex) {
            logger.error("校验ip异常！", ex.getMessage());
            boolean isValid = HttpStatus.HTTP_OK == status;
            ipInfo.setWheVaild(isValid);
            return ipInfo;
        }
        boolean isValid = HttpStatus.HTTP_OK == status;
        ipInfo.setWheVaild(isValid);
        return ipInfo;

    }


    /**
     * 请求校验 包含身份验证的代理ip
     *
     * @param ip
     * @param authRequest
     * @param timeout
     * @param isForeign
     * @return
     */
    public Integer request(String ip, VerifyIpRequest.AuthRequest authRequest, int timeout, boolean isForeign) {

        String[] split = ip.split(":");
        String hostname;
        int port;
        if (split.length != 2) {
            return 500;
        } else {
            hostname = split[0];
            port = Integer.parseInt(split[1]);
        }
        String pingUrl = isForeign ? customConfig.getForeignUrl() : customConfig.getMainUrl();
        HttpHost proxy = new HttpHost(hostname, port, "http");
        CredentialsProvider provider = new BasicCredentialsProvider();
        //包含账号密码的代理
        provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(authRequest.getName(), authRequest.getPwd()));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
        CloseableHttpResponse response;
        String resultString;
        try {
            HttpGet httpGet = new HttpGet(pingUrl);
            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);
            resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("代理Ip : { " + ip + " } ==> 调用url:" + pingUrl + "发生异常:" + e.getMessage());
            return 500;
        }
//        logger.info("返回url:{},response:{}", pingUrl, resultString);
//        System.out.println(response.getStatusLine().getStatusCode());
        return response.getStatusLine().getStatusCode();
    }


    /**
     * 请求校验  单个代理ip
     *
     * @param ip
     * @param timeout
     * @param isForeign
     * @return
     */
    public Integer request(String ip, int timeout, boolean isForeign) {

        String[] split = ip.split(":");
        if (split.length != 2) {
            return 500;
        }
        InetSocketAddress addr = new InetSocketAddress(split[0], Integer.valueOf(split[1]));
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        String pingUrl = isForeign ? customConfig.getForeignUrl() : customConfig.getMainUrl();
        HttpResponse execute = HttpRequest.get(pingUrl)
                .setProxy(proxy)
                .timeout(timeout)
                .execute();

        return execute.getStatus();
    }


    /**
     * @param ip
     * @param timeout
     * @return
     */
    public boolean ipIsValid(String ip, int timeout, boolean foreign) {
        try {
            Integer status = request(ip, timeout, foreign);
            if (status == HttpStatus.HTTP_OK) {
                return true;
            }
        } catch (Exception ex) {
            logger.warn(ip + " 检查失败！！！", ex.getMessage());
        }
        return false;
    }

    public boolean ipIsVaild(String ip, VerifyIpRequest.AuthRequest authRequest, int timeout, boolean foreign) {

        try {
            Integer status = request(ip, authRequest, timeout, foreign);
            if (status == HttpStatus.HTTP_OK) {
                return true;
            }
        } catch (Exception ex) {
            logger.warn(ip + "ip 无效", ex.getMessage());
        }

        return false;
    }


    /**
     * 获取有效的 ip
     *
     * @param ipInfos 需要验证的ip
     * @param timeOut 请求时长
     * @return
     */
    public List<IpInfo> checkIp(List<IpInfo> ipInfos, int timeOut) {

        this.timeOut = timeOut;
        if (CollUtil.isNotEmpty(ipInfos)) {
            //Executors.newCachedThreadPool()
            ThreadPoolExecutor es = new ThreadPoolExecutor(10, 200, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            try {
                List<Future<IpInfo>> futures = new ArrayList<>();
                for (IpInfo ipInfo : ipInfos) {
                    Future<IpInfo> submit = es.submit(new FactorialCalculator(ipInfo));
                    futures.add(submit);
                }
                /**
                 * 等待返回结果
                 */
                while (es.getCompletedTaskCount() < futures.size()) {
                    Thread.sleep(500);
                }
                List<IpInfo> validIps = new ArrayList<>();
                for (Future<IpInfo> future : futures) {
                    IpInfo validIp = future.get();
                    if (validIp != null) {
                        validIps.add(validIp);
                    }
                }
                return validIps;
            } catch (Exception e) {
                logger.error("检查Ip异常，请检查配置！！！", e);
            } finally {
                es.shutdown();
            }
        }
        return null;
    }


    /**
     * 结果回调
     */
    class FactorialCalculator implements Callable<IpInfo> {
        IpInfo ipInfo;

        public FactorialCalculator(IpInfo ip) {
            this.ipInfo = ip;
        }

        @Override
        public IpInfo call() throws Exception {
            if (ipInfo != null) {
                IpInfo result = checkIpValid(ipInfo, timeOut);
                return result;
            }
            return null;
        }
    }


    /**
     * 获取代理ip
     *
     * @return
     */
    public List<IpInfo> getProxyIps() {
        String ips = "183.154.49.22:9999,114.239.253.64:9999,36.22.79.130:9999,117.57.91.25:9999,36.22.78.137:9999,121.226.215.175:9999,117.26.45.136:9999,114.239.254.131:9999,117.69.201.107:9999,59.57.38.194:9999,183.154.52.105:9999,113.124.92.201:9999,49.70.17.235:9999,110.243.22.74:9999,117.95.200.33:31394,114.239.172.251:9999,183.164.239.92:35873,114.239.148.193:808,114.239.151.60:808,183.164.239.53:9999,61.128.208.94:3128,106.14.14.20:3128,218.2.226.42:80,222.218.122.5:9999,210.26.49.88:3128,118.126.15.136:8080,114.249.118.1:9000,115.195.89.54:8118,122.224.65.201:3128,111.160.169.54:42626,112.95.205.137:8888,153.101.64.50:12034,125.46.0.62:53281,118.89.234.236:8787,59.36.10.52:3128,116.252.39.176:53281,58.59.8.94:20179,122.224.65.198:3128,124.237.83.14:53281,1.196.161.46:9999,117.57.91.25:9999,59.57.38.194:9999,183.154.52.105:9999,113.124.92.201:9999,183.164.239.53:9999,183.154.48.115:9999,117.28.97.156:9999,121.226.188.249:9999,171.35.163.175:9999,115.221.245.217:9999,114.233.51.97:9999,123.160.68.190:9999,110.189.152.86:52277,180.122.224.241:9999,61.145.49.16:9999,117.95.232.43:9999,117.95.199.57:9999,113.194.29.179:9999,183.166.102.226:9999,114.239.254.161:9999,183.154.49.22:9999,114.239.253.64:9999,36.22.79.130:9999,36.22.78.137:9999,121.226.215.175:9999,117.26.45.136:9999,114.239.254.131:9999,117.69.201.107:9999,49.70.17.235:9999,110.243.22.74:9999,117.95.200.33:31394,114.239.172.251:9999,183.164.239.92:35873,114.239.148.193:808,114.239.151.60:808,106.110.212.44:9999,171.35.172.57:9999,113.120.38.48:9999,117.57.91.166:9999,114.239.250.37:9999,\n";
        String[] split = ips.split(",");
        List<IpInfo> infos = new ArrayList<>();
        for (String ip : split) {
            IpInfo info = new IpInfo();
            info.setIp(ip);
            info.setCheckTime(new Date());
            infos.add(info);
        }
        return infos;
    }


    /**
     * 获取代理IP剩余有效时长
     *
     * @return ip 剩余时长
     */
    public Map<String, Integer> getProxyIpRemains() {

        return null;
    }


}
