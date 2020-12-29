package com.jin10.spider.spiderserver.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.spiderserver.entity.PreUser;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.service.ISysUserService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Airey
 * @date 2019/11/11 12:00
 * ----------------------------------------------
 * JWT工具类
 * ----------------------------------------------
 */
@Slf4j
@Component
public class JwtUtil {


    private static final String USENAME = Claims.SUBJECT;

    private static final String USERID = "userid";

    private static final String CREATED = "created";

    private static final String AUTHORITIES = "authorities";

    private static final String SECRET = "abcdefgh";

    private static final String PASSWORD = "password";

    @Autowired
    private ISysUserService userService;

    //设置token生效时间
    private static final long EXPIRE_TIME = 3600* 5 * 24 * 60 * 60 * 1000L;


    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String authTokenStart;

    /**
     * 生成令牌
     *
     * @param preUser
     * @return
     */
    public static String generateToken(PreUser preUser) {

        Map<String, Object> claims = new HashMap<>(5);
        claims.put(USERID, preUser.getUserId());
        claims.put(USENAME, preUser.getUsername());
        claims.put(PASSWORD, preUser.getPassword());
        claims.put(CREATED, new Date());
        claims.put(AUTHORITIES, preUser.getAuthorities());
        return generateToken(claims);
    }

    /**
     * 从数据声明令牌
     *
     * @param claims
     * @return
     */
    private static String generateToken(Map<String, Object> claims) {

        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

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
     * 根据请求令牌获取登录认证信息
     *
     * @param request
     * @return
     */
    public PreUser getUserFromToken(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {

            if (isTokenExpired(token)) {
                throw new BaseException("JWT Token已过期，请重新登陆！");
            }

            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return null;
            }
            String username = claims.getSubject();
            if (username == null) {
                return null;
            }

            //解析对应的权限以及用户id
            Object authors = claims.get(AUTHORITIES);
            Integer userId = ((Integer) claims.get(USERID));
            String password = claims.get(PASSWORD).toString();
            Long userIdL = userId.longValue();
            Set<String> perms = new HashSet<>();
            if (authors instanceof List) {
                for (Object object : (List) authors) {
                    perms.add(((Map) object).get("authority").toString());
                }
            }
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(perms.toArray(new String[0]));
            if (validateToken(username, password)) {
                return new PreUser(userIdL, username, password, authorities);
            }

        }
        return null;
    }

//    /**
//     * 验证令牌
//     *
//     * @param token
//     * @param username
//     * @return
//     */
//    private static boolean validateToken(String token, String username) {
//        String userName = getUsernameFromToken(token);
//        return (userName.equals(username)) && !isTokenExpired(token);
//    }

    /**
     * 验证令牌
     *
     * @param username
     * @param password
     * @return
     */
    public boolean validateToken(String username, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username).eq(SysUser::getPassword, password);
        SysUser one = userService.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(one)) {
            return true;
        }
        return false;
    }

    /**
     * 刷新令牌
     *
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        String refreshedToken;

        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;

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
     * 获取请求token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if ("/login".equals(requestURI) || "/partyPushMsg".equals(requestURI)
                || "/findMaintainerList".equals(requestURI) || "/bulkPushMsg".equals(requestURI)) {
            return null;
        }

        String auth = request.getParameter("auth");

        if (StringUtils.isNotBlank(auth)) {
            return auth;
        }

        String token = request.getHeader(tokenHeader);
        if (StringUtils.isNotEmpty(token)) {
            token = token.substring(authTokenStart.length());
        }
        return token;
    }


}
