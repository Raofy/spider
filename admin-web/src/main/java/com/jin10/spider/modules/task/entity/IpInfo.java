package com.jin10.spider.modules.task.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 代理ip 信息
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IpInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * ip:端口、113.120.61.166:22989
     */
    @NotNull
    private String ip;


    /**
     * 检验时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 是否国外网址
     */
    @TableField(value = "is_foreign")
    private boolean wheForeign;

    /**
     * 延迟
     */
    private Long delay;

    /**
     * 检验次数
     */
    private int checkTimes;

    /**
     * 地区/国家
     */
    private String area;

    /**
     * 代理平台
     */
    @NotNull
    private String platform;


    /**
     * 类型 http/ https/ socket
     */
    @NotNull
    private String type;


    private String extra;


    @JsonIgnore
    private Integer deleted;


    /**
     * 是否校验
     */
    @TableField(value = "is_valid")
    private boolean wheVaild;


    @NotNull
    @Range(min = 2, max = 3)
    private int proxyLevel = 2;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IpInfo)) {
            return false;
        }
        IpInfo ipInfo = (IpInfo) o;
        return getIp().equals(ipInfo.getIp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIp());
    }

    public static void main(String[] args) {

        List<IpInfo> list = new ArrayList<>();

        IpInfo ipInfo = new IpInfo();
        ipInfo.setIp("47.111.13.97:3245");
        list.add(ipInfo);
        IpInfo ipInfo1 = new IpInfo();
        ipInfo1.setIp("47.111.13.97:3245");

        IpInfo ipInfo2 = new IpInfo();
        ipInfo2.setIp("47.111.13.23:3245");
        list.add(ipInfo2);


        System.out.println(ipInfo.equals(ipInfo1));
        System.out.println(ipInfo.equals(ipInfo2));
        System.out.println(list.contains(ipInfo1));

    }

}
