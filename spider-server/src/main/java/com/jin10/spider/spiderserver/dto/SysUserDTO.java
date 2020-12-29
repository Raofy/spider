package com.jin10.spider.spiderserver.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Airey
 * @date 2019/11/14 10:29
 * ----------------------------------------------
 * 系统用户DTO
 * ----------------------------------------------
 */
@Data
public class SysUserDTO implements Serializable {

    private static final long serialVersionUID = -2357848503560954112L;

    /**
     * 主键id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态  0：禁用   1：正常
     */
    private Integer status;


    /**
     * 角色列表
     */
    private List<Long> roleList;



}
