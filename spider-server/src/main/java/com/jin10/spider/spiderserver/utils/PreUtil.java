package com.jin10.spider.spiderserver.utils;


import com.jin10.spider.spiderserver.entity.PreUser;
import lombok.experimental.UtilityClass;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.LinkedList;

import java.util.Random;

/**
 * @author zxlei
 * @date 2019/11/14  10:41
 * ----------------------------------------------
 * 系统用户工具类
 * ----------------------------------------------
 */
@UtilityClass
public class PreUtil {


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param rawPass
     * @return
     */
    public String encode(String rawPass) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPass);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public PreUser getCurrentUser() {
        return (PreUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 校验密码
     *
     * @param newPass
     * @param passwordEncoderOldPass
     * @return
     */
    public boolean validatePass(String newPass, String passwordEncoderOldPass) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(newPass, passwordEncoderOldPass);
    }

    /**
     * 不重复的验证码
     *
     * @param i
     * @return
     */
    public String codeGen(int i) {
        char[] codeSequence = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I',
                'O', 'P', 'L', 'K', 'J', 'H', 'G', 'F', 'D',
                'S', 'A', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        while (true) {
            // 随机产生一个下标，通过下标取出字符数组中对应的字符
            char c = codeSequence[random.nextInt(codeSequence.length)];
            // 假设取出来的字符在动态字符中不存在，代表没有重复的
            if (stringBuilder.indexOf(c + "") == -1) {
                stringBuilder.append(c);
                count++;
                //控制随机生成的个数
                if (count == i) {
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        String aa123456 = encode("Aa123456");
        System.out.println(aa123456);
    }
}
