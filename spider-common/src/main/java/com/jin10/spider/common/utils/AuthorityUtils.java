package com.jin10.spider.common.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Airey
 * @date 2019/11/11 12:00
 * ----------------------------------------------
 * JWT工具类
 * ----------------------------------------------
 */
@Slf4j
@Component
public class AuthorityUtils {


    private static final String SECRET = "abcdefgh";


    /**
     * 从令牌中获取用户名
     *
     * @param token
     * @return
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }


    /**
     * 从令牌中获取数据声明
     *
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    /**
     * 判断令牌是否过期
     *
     * @param token
     * @return
     */
    private static boolean isTokenExpired(String token) {

        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }

    }


    /**
     * 通过 token 验证是否有效并 获取用户名
     * @param token
     * @return
     */
    public static String getValidedUserFromToken(String token) {
        if (StringUtils.isNotBlank(token) && !isTokenExpired(token)) {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        }
        return null;
    }


}
