package com.jin10.spider.spiderserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.spiderserver.dto.SysUserDTO;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.form.SysUserForm;
import com.jin10.spider.spiderserver.dto.SysUserInfoDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface ISysUserService extends IService<SysUser> {


    /**
     * 用户登录接口
     *
     * @param username
     * @param password
     * @param request
     * @return
     */
    String login(String username, String password, HttpServletRequest request);


    /**
     * 新增用户
     *
     * @param sysUserDTO
     * @return
     */
    boolean insertUser(SysUserDTO sysUserDTO);


    /**
     * 根据主键删除用户
     *
     * @param userId
     * @return
     */
    boolean deleteUser(Long userId);


    /**
     * 更新系统用户信息
     *
     * @param userForm
     * @return
     */
    boolean updateUser(SysUserForm userForm);


    /**
     * 查询用户列表
     *
     * @param userForm
     * @return
     */
    IPage<SysUser> findSysUserList(SysUserForm userForm);

    /**
     * 根据用户Id查询权限
     *
     * @param userId
     * @return
     */
    Set<String> findPermsByUserId(Long userId);


    /**
     * 通过用户名查找用户
     *
     * @param username
     * @return
     */
    SysUser findByUsername(String username);


    /**
     * 通过用户id查询角色集合
     *
     * @param userId
     * @return
     */
    Set<String> findRoleIdByUserId(Long userId);


    /**
     * 获取用户具体详细消息
     *
     * @return
     */
    SysUserInfoDTO getUserInfo();


    /**
     * 修改用户信息
     *
     * @param sysUser
     * @return
     */
    boolean updateUserInfo(SysUser sysUser);


    /**
     * 查询维护人员账户列表
     *
     * @return
     */
    Set<String> findMaintainerUsername();


    /**
     * 查询维护人员列表
     *
     * @return
     */
    List<SysUser> findMaintainerList();


    /**
     * 根据维护人员分配模板id
     */
    void assignTempId();


    /**
     * 检查手机号是否在职
     *
     * @param phone
     * @return
     */
    boolean checkPhonemoj(String phone);


}
