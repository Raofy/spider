package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.spiderserver.bean.Maintainer;
import com.jin10.spider.spiderserver.config.CustomConfig;
import com.jin10.spider.spiderserver.dto.SysUserDTO;
import com.jin10.spider.spiderserver.entity.*;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.spiderserver.form.SysUserForm;
import com.jin10.spider.spiderserver.mapper.SysUserMapper;
import com.jin10.spider.spiderserver.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.spiderserver.utils.JwtUtil;
import com.jin10.spider.spiderserver.utils.PreUtil;
import com.jin10.spider.spiderserver.dto.SysUserInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.security.auth.login.AccountExpiredException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {


    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CustomConfig customConfig;
    @Autowired
    private ISysUserGroupService sysUserGroupService;
    @Autowired
    private ISysGroupService sysGroupService;


    /**
     * 用户登录接口
     *
     * @param username
     * @param password
     * @param request
     * @return
     */
    @Override
    public String login(String username, String password, HttpServletRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw new BaseException("用户名或密码错误", 402);
            } else if (e instanceof DisabledException) {
                throw new BaseException("账户被禁用", 402);
            } else if (e instanceof AccountExpiredException) {
                throw new BaseException("账户过期无法验证", 402);
            } else {
                throw new BaseException("账户被锁定,无法登录", 402);
            }
        }
        //存储认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //生成token
        PreUser userDetail = (PreUser) authentication.getPrincipal();
        return JwtUtil.generateToken(userDetail);
    }

    /**
     * 新增用户
     *
     * @param sysUserDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertUser(SysUserDTO sysUserDTO) {
        String username = sysUserDTO.getUsername();
        SysUser byUsername = this.findByUsername(username);
        if (ObjectUtil.isNotNull(byUsername)) {
            throw new BaseException("该用户已经存在！！！");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserDTO, sysUser);
        sysUser.setPassword(PreUtil.encode(sysUser.getPassword()));
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        baseMapper.insert(sysUser);
        List<SysUserRole> userRoleList = sysUserDTO.getRoleList().stream().map(item -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(item);
            sysUserRole.setUserId(sysUser.getUserId());
            return sysUserRole;
        }).collect(Collectors.toList());
        boolean row = userRoleService.saveBatch(userRoleList);

        //添加默认分组
        sysUserGroupService.saveDefault(sysUser.getUserId());

        return row;
    }

    /**
     * 根据主键删除用户
     *
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUser(Long userId) {

        baseMapper.deleteById(userId);
        boolean remove = userRoleService.lambdaUpdate().eq(SysUserRole::getUserId, userId).remove();
        return remove;
    }

    /**
     * 更新系统用户信息
     *
     * @param userForm
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(SysUserForm userForm) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userForm, sysUser);
        sysUser.setUpdateTime(new Date());
        baseMapper.updateById(sysUser);
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, sysUser.getUserId()));
        List<SysUserRole> userRoleList = userForm.getRoleList().stream().map(item -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(item);
            sysUserRole.setUserId(sysUser.getUserId());
            return sysUserRole;
        }).collect(Collectors.toList());
        return userRoleService.saveBatch(userRoleList);
    }

    /**
     * 查询用户列表
     *
     * @param userForm
     * @return
     */
    @Override
    public IPage<SysUser> findSysUserList(SysUserForm userForm) {
        Page<SysUser> page = new Page<>(userForm.getPageNum(), userForm.getPageSize());
        IPage<SysUser> sysUserList = baseMapper.findSysUserList(page);
        return sysUserList;
    }

    /**
     * 根据用户Id查询权限
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> findPermsByUserId(Long userId) {
        return menuService.findPermsByUserId(userId).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
    }

    /**
     * 通过用户名查找用户
     *
     * @param username
     * @return
     */
    @Override
    public SysUser findByUsername(String username) {
        return baseMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .select(SysUser::getUserId, SysUser::getUsername, SysUser::getPassword, SysUser::getStatus,SysUser::getPhone)
                .eq(SysUser::getUsername, username));
    }

    /**
     * 通过用户id查询角色集合
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> findRoleIdByUserId(Long userId) {
        return userRoleService.selectUserRoleListByUserId(userId)
                .stream()
                .map(sysUserRole -> "ROLE_" + sysUserRole.getRoleId())
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户具体信息
     *
     * @return
     */
    @Override
    public SysUserInfoDTO getUserInfo() {

        PreUser currentUser = PreUtil.getCurrentUser();
        SysUserInfoDTO userInfo = baseMapper.getUserInfo(currentUser.getUserId());

        List<SysMenu> menuList = userInfo.getMenuList();

        List<SysMenu> rootList = menuList.stream().filter(x -> x.getParentId() == 0).collect(Collectors.toList());
        List<SysMenu> bodyList = menuList.stream().filter(x -> x.getParentId() != 0).collect(Collectors.toList());
        List<SysMenu> permissionTree = getPermissionTree(bodyList, rootList);
        userInfo.setMenuList(permissionTree);
        //获取当前分组
        SysUserGroup userGroup = sysUserGroupService.getOne(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getUserId, currentUser.getUserId()));
        //获取分组信息
        SysGroup group = sysGroupService.getById(userGroup.getGroupId());
        userInfo.setGroupName(group.getName());

        return userInfo;
    }

    /**
     * 修改用户信息
     *
     * @param sysUser
     * @return
     */
    @Override
    public boolean updateUserInfo(SysUser sysUser) {
        return baseMapper.updateById(sysUser) > 0;
    }

    /**
     * 查询维护人员列表
     *
     * @return
     */
    @Override
    public Set<String> findMaintainerUsername() {
        List<SysUser> maintainer = baseMapper.findMaintainer();
        Set<String> maintainerSet = maintainer.stream().map(SysUser::getUsername).collect(Collectors.toSet());
        return maintainerSet;
    }

    /**
     * 查询维护人民电话列表
     *
     * @return
     */
    @Override
    public List<SysUser> findMaintainerList() {
        return baseMapper.findMaintainer();
    }


    /**
     * 根据维护人员分配模板id
     */
    @Override
    public void assignTempId() {
        Set<String> maintainerSet = this.findMaintainerUsername();
        if (CollUtil.isEmpty(maintainerSet)) {
            return;
        }
        redisUtils.del(RedisKey.MAINTAINER_MAP);
        PriorityBlockingQueue<Maintainer> maintainerPriorityBlockingQueue = new PriorityBlockingQueue<>();
        maintainerSet.forEach(item -> {
            Maintainer maintainer = new Maintainer(item, 100L);
            maintainerPriorityBlockingQueue.add(maintainer);
        });
        String realUrl = customConfig.getAdminUrl() + "/admin/template/getTemplateIdList";
        BaseResponse baseResponse = getBaseResponse(realUrl);
        if (HttpStatus.HTTP_OK == baseResponse.getCode()) {
            if (baseResponse.getData() instanceof JSONObject) {
                JSONObject data = (JSONObject) baseResponse.getData();
                JSONArray jsonArray = data.getJSONArray("tempIdList");
                if (!jsonArray.isEmpty()) {
                    jsonArray.stream().forEach(tempId -> {
                        try {
                            Maintainer take = maintainerPriorityBlockingQueue.take();
                            Set<Integer> usernameSet = (Set<Integer>) redisUtils.hget(RedisKey.MAINTAINER_MAP, take.getUsername());
                            if (CollUtil.isEmpty(usernameSet)) {
                                usernameSet = new HashSet<>();
                            }
                            usernameSet.add((Integer) tempId);
                            redisUtils.hset(RedisKey.MAINTAINER_MAP, take.getUsername(), usernameSet);
                            take.setSystemTime(System.currentTimeMillis());
                            maintainerPriorityBlockingQueue.add(take);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    });

                }
            }
        }


    }

    /**
     * 检查手机号是否在职
     *
     * @param phone
     * @return
     */
    @Override
    public boolean checkPhonemoj(String phone) {

        String check = customConfig.getCheck();
        String url = "https://admin-ucenter-api.jin10.com/inner/moj";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("x-version", "2.0.0");
        headerMap.put("x-app-id", "eAo8oLYybZrX6lZb");
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("m", phone);
        formMap.put("check", check);
        HttpResponse execute = HttpRequest.get(url).headerMap(headerMap, false).form(formMap).timeout(5000).execute();
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            JSONObject jsonObject = JSONObject.parseObject(execute.body());
            boolean data = jsonObject.getBooleanValue("data");
            if (ObjectUtil.isNotNull(data)) {
                return data;
            }
        }
        return false;
    }


    /**
     * 获取返回响应体
     *
     * @param realUrl
     * @return
     */
    private BaseResponse getBaseResponse(String realUrl) {
        BaseResponse response = new BaseResponse();
        HttpResponse execute = HttpRequest.get(realUrl).timeout(5000).execute();
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            String body = execute.body();
            if (JSONObject.isValid(body)) {
                JSONObject resultJson = JSONObject.parseObject(body);
                response = resultJson.toJavaObject(BaseResponse.class);
            }
        } else {
            return BaseResponse.error(execute.getStatus(), execute.body());
        }
        return response;
    }

    /**
     * 获取权限树
     *
     * @param bodyList
     * @param rootList
     * @return
     */
    private List<SysMenu> getPermissionTree(List<SysMenu> bodyList, List<SysMenu> rootList) {

        if (bodyList != null && !bodyList.isEmpty()) {
            //声明一个map，用来过滤已操作过的数据
            Map<Long, Long> map = new HashMap<>(bodyList.size());
            rootList.forEach(beanTree -> getChild(beanTree, map, bodyList));
            return rootList;
        } else if (CollUtil.isNotEmpty(rootList)) {
            rootList.forEach(item -> item.setChilds(new ArrayList<>()));
            return rootList;
        }


        return null;
    }

    /**
     * 获取子节点
     *
     * @param beanTree
     * @param map
     * @param bodyList
     */
    private void getChild(SysMenu beanTree, Map<Long, Long> map, List<SysMenu> bodyList) {
        List<SysMenu> childList = new ArrayList<>();
        bodyList.stream().filter(c -> !map.containsKey(c.getMenuId()))
                .filter(c -> c.getParentId().equals(beanTree.getMenuId()))
                .forEach(c -> {
                    map.put(c.getMenuId(), c.getParentId());
                    getChild(c, map, bodyList);
                    childList.add(c);
                });
        beanTree.setChilds(childList);
    }


}
