package com.jin10.spider.spiderserver.filter;

import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.spiderserver.entity.PreUser;
import com.jin10.spider.spiderserver.service.ISysUserService;
import com.jin10.spider.spiderserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * @author Airey
 * @date 2019/11/11 15:16
 * ----------------------------------------------
 * token过滤器来验证token有效性 引用的stackoverflow一个答案里的处理方式
 * ----------------------------------------------
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ISysUserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        PreUser securityUser = jwtUtil.getUserFromToken(httpServletRequest);
        if (ObjectUtil.isNotNull(securityUser)) {
            Set<String> permissions = userService.findPermsByUserId(securityUser.getUserId());
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(new String[0]));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityUser, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
