package com.jin10.spider.spiderserver.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.exception.BaseNoInfoException;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.spiderserver.constants.GlobalConstants;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.message.MqReceiver;
import com.jin10.spider.spiderserver.service.ISysUserService;
import com.jin10.spider.spiderserver.utils.IpAddressUtil;
import com.jin10.spider.spiderserver.utils.PreUtil;
import com.jin10.spider.spiderserver.utils.SecurityUtil;
import com.jin10.spider.spiderserver.dto.SysUserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import java.util.regex.Matcher;

/**
 * @author Airey
 * @date 2019/11/8 17:44
 * ----------------------------------------------
 * 用户登陆校验处理
 * ----------------------------------------------
 */
@RestController
@Slf4j
public class LoginController {


    @Autowired
    private ISysUserService userService;

    @Autowired
    private MqReceiver mqReceiver;

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 用户登录接口
     *
     * @param sysUser
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse login(@RequestBody SysUser sysUser, HttpServletRequest request, HttpServletResponse response) {

        SysUser byUsername = userService.findByUsername(sysUser.getUsername());

        String phone = byUsername.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new BaseException("登陆失败！手机号为空，校验无法通过，请检查！！！");
        } else {
            boolean checkPhonemoj = userService.checkPhonemoj(phone);
            if (!checkPhonemoj) {
                throw new BaseException("登陆失败！手机号为不在职状态！！！");
            }
        }

        String token = userService.login(sysUser.getUsername(), sysUser.getPassword(), request);
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/login;HttpOnly");

        return BaseResponse.ok(token);
    }


    /**
     * 获取用户具体信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public BaseResponse getUserInfo() {
        SysUserInfoDTO userInfo = userService.getUserInfo();
        return BaseResponse.ok(userInfo);
    }


    /**
     * 查询维护人员列表
     *
     * @return
     */
    @GetMapping("findMaintainerList")
    public BaseResponse findMaintainerList() {
        List<SysUser> maintainerList = userService.findMaintainerList();
        maintainerList.forEach(item -> item.setRoleList(null));
        return BaseResponse.ok(maintainerList);
    }

    /**
     * 刷新维护人员管理模板列表
     *
     * @return
     */
    @GetMapping("assignTempId")
    public BaseResponse assignTempId() {
        userService.assignTempId();
        return BaseResponse.ok();
    }


    /**
     * 修改密码
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/updatePass")
    public BaseResponse updatePass(@RequestBody Map<String, Object> paramMap) {
        //校验密码流程
        String oldPass = MapUtil.getStr(paramMap, "oldPass");
        String newPass = MapUtil.getStr(paramMap, "newPass");
        SysUser sysUser = userService.findByUsername(SecurityUtil.getUser().getUsername());
        if (!PreUtil.validatePass(oldPass, sysUser.getPassword())) {
            throw new BaseException("原密码错误！");
        }
        if (StrUtil.endWith(oldPass, newPass)) {
            throw new BaseException("新密码不能与旧密码相同！");
        }
        //修改密码流程
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setPassword(PreUtil.encode(newPass));
        boolean b = userService.updateById(user);
        return BaseResponse.ok(b);
    }


    /**
     * 第三方推送
     *
     * @return
     */
    @GetMapping("partyPushMsg")
    public BaseResponse pushMessage(HttpServletRequest request) {

        String url = request.getParameter("url");
        String title = request.getParameter("title");
        if (redisUtils.hasKey(RedisKey.HASH_CODE_TITLE + title.hashCode())) {
            throw new BaseNoInfoException("消息重复,已经推送过了！！!");
        } else {
            redisUtils.set(RedisKey.HASH_CODE_TITLE + title.hashCode(), title, 3 * 24 * 60 * 60L);
        }
        String source = request.getParameter("source");
        Long time = Long.valueOf(request.getParameter("time"));
        String category = request.getParameter("category");
        if (StringUtils.isBlank(title) || StringUtils.isBlank(source)
                || ObjectUtil.isNull(time) || StringUtils.isBlank(category)) {
            throw new BaseException("传递的参数错误！！！请检查");
        }

        if (StrUtil.containsIgnoreCase(source, "app")) {
            log.info("自动过滤掉包含app的消息");
            return BaseResponse.ok();
        }

        if ("新浪微博".equals(source)) {
            String[] split = title.split(":");
            if (split.length > 0) {
                url = "http://weibo.com/n/" + split[0].trim();
            }
            if (title.contains("http") || title.contains("https")) {
                Matcher matcher = GlobalConstants.HTTP_PATTERN.matcher(title);
                if (matcher.find()) {
                    String group = matcher.group();
                    String format = StrUtil.format(GlobalConstants.HTTP_TEMP, group, group);
                    title = title.replace(group, format);
                }
            }
        }

        JSONObject data = new JSONObject();
        data.put("url", url);
        data.put("title", title);
        data.put("source", source);
        data.put("category", category);
        data.put("channel", "第三方推送快讯");
        data.put("time", time);
        handleMsg(data);
        mqReceiver.dealMsg(data.toJSONString());

        return BaseResponse.ok();
    }


    /**
     * 批量推送消息
     *
     * @param msgJson
     */
    @PostMapping("/bulkPushMsg")
    public BaseResponse pushBulkMsg(@RequestBody JSONObject msgJson) {
        log.info("msgJson => " + msgJson);
        boolean repeat = msgJson.getBoolean("repeat");
        JSONArray data = msgJson.getJSONArray("data");
        String channel = msgJson.getString("channel");
        String source = msgJson.getString("source");
        if (CollUtil.isEmpty(data)) {
            log.error("传递参数错误！！！");
            throw new BaseNoInfoException("传递参数错误！！！");
        }
        if (repeat) {
            JSONArray proData = new JSONArray();
            data.forEach(item -> {
                String title = (String) ((LinkedHashMap) item).get("title");
                String key = title + channel + source;
                String md5Key = SecureUtil.md5(key);
                if (redisUtils.hasKey(RedisKey.HASH_CODE_BULK + md5Key)) {
                    log.info("消息重复了！！！");
                } else {
                    redisUtils.set(RedisKey.HASH_CODE_BULK + md5Key, title);
                    proData.add(item);
                }
            });
            if (CollUtil.isEmpty(proData)) {
                return BaseResponse.ok();
            } else {
                msgJson.put("data", proData);
            }
        }
        msgJson.put("taskId", IdUtil.fastSimpleUUID());
        mqReceiver.dealMsg(msgJson.toJSONString());
        return BaseResponse.ok();
    }


    /**
     * 模块化消息
     *
     * @param jsonObject
     */
    private void handleMsg(JSONObject jsonObject) {
        jsonObject.put("taskId", IdUtil.fastSimpleUUID());
        JSONArray jsonArray = new JSONArray();
        JSONObject resultJ = new JSONObject();
        resultJ.put("url", jsonObject.getString("url"));
        resultJ.put("title", jsonObject.getString("title"));
        jsonArray.add(resultJ);
        jsonObject.put("data", jsonArray);

    }

    /**
     * 取消MQ，改用http协议
     * @param request
     * @param message
     * @return
     */
    @PostMapping("handlerMsg")
    public BaseResponse handlerMsg(HttpServletRequest request,@RequestBody String message){
        String ipAddress = IpAddressUtil.getIpAddress(request);
        log.info("处理爬虫请求消息的ip： {} ,message : {}"  ,ipAddress,message);
        mqReceiver.dealMsg(message);
        return BaseResponse.ok();
    }

}
