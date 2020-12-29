package com.jin10.spider.modules.statistics.bean;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hongda.fang
 * * @date 2019-12-09 15:11
 * ----------------------------------------------
 * 爬虫服务器信息
 */
@Data
@Document(indexName = "server_info_log", type = "logs", shards = 1, replicas = 0)
public class ServerInfo implements Serializable {


    /**
     * system : {"hostname":"iZbp1fuptjomb7x6c3kr8pZ","os_name":"Linux","linux_distro":"CentOS Linux 7.3.1611","platform":"64bit"}
     * load : {"min5":2.89,"min15":2.79}
     * mem : {"total":3705348096,"percent":89.1}
     * ip : {"public_address":"115.29.215.82"}
     * cpu : {"cpucore":2,"total":74.6,"system":6,"user":23}
     * fs : {"/.size":42139451392,"/.percent":31.7}
     * uptime : {"seconds":13065235}
     * network : {"eth1.interface_name":"eth1","eth1.tx":98727,"eth1.rx":108113,"eth0.rx":3572,"eth0.tx":1844,"eth0.interface_name":"eth0"}
     */

    /**
     * 系统信息
     */
    @Id
    private String logId = IdUtil.fastSimpleUUID();

    @Field(type = FieldType.Long) // 指定存储格式
    private Date createTime = new Date();


    @Field(type = FieldType.Object)
    private SystemEntity system;

    /**
     * 负载
     */
    @Field(type = FieldType.Object)
    private LoadEntity load;

    /**
     * 内存
     */
    @Field(type = FieldType.Object)
    private MemEntity mem;

    /**
     * 网卡信息
     */
    @Field(type = FieldType.Object)
    private IpEntity ip;

    /**
     * cpu 信息
     */
    @Field(type = FieldType.Object)
    private CpuEntity cpu;

    /**
     * 磁盘信息
     */
    @Field(type = FieldType.Object)
    private FsEntity fs;

    /**
     * 网络情况
     */
    @Field(type = FieldType.Object)
    private NetworkEntity network;

    @Field(type = FieldType.Object)
    private UptimeEntity uptime;


    private boolean isWarned;


    @Data
    public class UptimeEntity {

        @Field(type = FieldType.Long)
        private long seconds;

    }

    @Data
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    public class NetworkEntity {


        /**
         * eth1Tx : 98727
         * eth0Rx : 3572
         * eth1Rx : 108113
         * eth0Tx : 1844
         * eth0InterfaceName : eth0
         * eth1InterfaceName : eth1
         */
        @JsonProperty("eth1.tx")
        @Field(type = FieldType.Integer)
        int eth1Tx;

        @JsonProperty("eth0.tx")
        @Field(type = FieldType.Integer)
        int eth0Rx;

        @JsonProperty("eth1.rx")
        @Field(type = FieldType.Integer)
        int eth1Rx;

        @JsonProperty("eth0.rx")
        @Field(type = FieldType.Integer)
        int eth0Tx;

        @JsonProperty("eth0.interface_name")
        @Field(type = FieldType.Keyword)
        String eth0InterfaceName;

        @JsonProperty("eth1.interface_name")
        @Field(type = FieldType.Keyword)
        String eth1InterfaceName;

    }

    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    public static class FsEntity {
        /**
         * size : 42139451392
         * percent : 31.7
         */
        @JsonProperty("/.size")
        @Field(type = FieldType.Long)
        public long size;

        @JsonProperty("/.percent")
        @Field(type = FieldType.Float)
        public double percent;

        @JsonProperty("/data.size")
        @Field(type = FieldType.Long)
        private long dataSize;

        @JsonProperty("/data.percent")
        @Field(type = FieldType.Double)
        public double dataPercent;


        public void setSize(long size) {
            this.size = size;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public long getSize() {
            return size;
        }

        public double getPercent() {
            return percent;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public double getDataPercent() {
            return dataPercent;
        }

        public void setDataPercent(double dataPercent) {
            this.dataPercent = dataPercent;
        }
    }

    public static class SystemEntity {
        /**
         * hostname : iZbp1fuptjomb7x6c3kr8pZ
         * os_name : Linux
         * linux_distro : CentOS Linux 7.3.1611
         * platform : 64bit
         */
        @Field(type = FieldType.Text)
        private String hostname;
        @Field(type = FieldType.Text)
        private String os_name;
        @Field(type = FieldType.Text)
        private String linux_distro;
        @Field(type = FieldType.Text)
        private String platform;

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public void setOs_name(String os_name) {
            this.os_name = os_name;
        }

        public void setLinux_distro(String linux_distro) {
            this.linux_distro = linux_distro;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getHostname() {
            return hostname;
        }

        public String getOs_name() {
            return os_name;
        }

        public String getLinux_distro() {
            return linux_distro;
        }

        public String getPlatform() {
            return platform;
        }
    }

    public static class LoadEntity {
        /**
         * min5 : 2.89
         * min15 : 2.79
         */
        @Field(type = FieldType.Float)
        private double min5;
        @Field(type = FieldType.Float)
        private double min15;

        public void setMin5(double min5) {
            this.min5 = min5;
        }

        public void setMin15(double min15) {
            this.min15 = min15;
        }

        public double getMin5() {
            return min5;
        }

        public double getMin15() {
            return min15;
        }
    }

    public static class MemEntity {
        /**
         * total : 3705348096
         * percent : 89.1
         */
        @Field(type = FieldType.Long)
        private long total;
        @Field(type = FieldType.Float)
        private double percent;

        public void setTotal(long total) {
            this.total = total;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public long getTotal() {
            return total;
        }

        public double getPercent() {
            return percent;
        }
    }

    public static class IpEntity {
        /**
         * public_address : 115.29.215.82
         */
        @Field(type = FieldType.Text)
        private String public_address;

        public void setPublic_address(String public_address) {
            this.public_address = public_address;
        }

        public String getPublic_address() {
            return public_address;
        }
    }

    public static class CpuEntity {
        /**
         * cpucore : 2
         * total : 74.6
         * system : 6
         * user : 23
         */
        @Field(type = FieldType.Long)
        private int cpucore;
        @Field(type = FieldType.Float)
        private double total;
        @Field(type = FieldType.Long)
        private int system;
        @Field(type = FieldType.Long)
        private int user;

        public void setCpucore(int cpucore) {
            this.cpucore = cpucore;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public void setSystem(int system) {
            this.system = system;
        }

        public void setUser(int user) {
            this.user = user;
        }

        public int getCpucore() {
            return cpucore;
        }

        public double getTotal() {
            return total;
        }

        public int getSystem() {
            return system;
        }

        public int getUser() {
            return user;
        }
    }
}
