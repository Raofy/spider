package com.jin10.spider.spiderserver.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Airey
 * @date 2019/11/14 15:09
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Data
public class SysRoleDTO implements Serializable {

    private static final long serialVersionUID = -847457322129017763L;


    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 对应的菜单列表
     */
    private List<Long> sysRoleMenuList;




}
