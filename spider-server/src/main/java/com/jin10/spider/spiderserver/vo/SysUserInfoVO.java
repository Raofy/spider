package com.jin10.spider.spiderserver.vo;

import com.jin10.spider.spiderserver.entity.SysMenu;
import com.jin10.spider.spiderserver.entity.SysRole;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Airey
 * @date 2019/12/12 10:30
 * ----------------------------------------------
 * 系统用户信息封装类 VO对象
 * <p>
 * ----------------------------------------------
 */
@Data
public class SysUserInfoVO implements Serializable {


    private static final long serialVersionUID = 3694239148442260616L;
    /**
     * 主键id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

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
    private List<SysRole> roleList;

    /**
     * 菜单列表
     */
    private List<SysMenu> menuList;




}
