package com.jin10.spider.common.controller;

import com.jin10.spider.common.utils.AuthorityUtils;

/**
 * @author hongda.fang
 * @date 2019-12-18 19:00
 * ----------------------------------------------
 */
public class AbstractController {

    public String getUserName(String jwt) {
        return AuthorityUtils.getUsernameFromToken(jwt);
    }

}
