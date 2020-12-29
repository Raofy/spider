package com.jin10.spider.spiderserver.form;

import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

import java.util.List;

/**
 * @author Airey
 * @date 2019/12/12 13:49
 * ----------------------------------------------
 * 用户信息条件封装
 * ----------------------------------------------
 */
@Data
public class SysUserForm extends BasePageRequest {

    /**
     * 用户id
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
     * 电话
     */
    private String phone;


    /**
     * 是否维护人员
     */
    private Boolean maintainer;


    /**
     * 角色列表
     */
    private List<Long> roleList;


}
