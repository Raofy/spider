package com.jin10.spider.spiderserver.config;

import com.jin10.spider.spiderserver.filter.JwtAuthenticationTokenFilter;
import com.jin10.spider.spiderserver.handler.AuthenticationEntryPointImpl;
import com.jin10.spider.spiderserver.utils.UserDetailsServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
/**
 * @author Airey
 * @date 2019/11/11 16:38
 * ----------------------------------------------
 * Security配置类
 * ----------------------------------------------
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    private UserDetailsServiceUtil userDetailsServiceUtil;

    /**
     * 解决 无法直接注入 AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**@
     * 配置策略
     *
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .cors().and().csrf().disable()
                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 对于登录login 图标 要允许匿名访问
                .antMatchers("/login/**", "/favicon.ico").anonymous()
                .antMatchers("/findMaintainerList/**", "/favicon.ico").anonymous()
                .antMatchers("/partyPushMsg/**", "/favicon.ico").anonymous()
                .antMatchers("/bulkPushMsg/**", "/favicon.ico").anonymous()
                .antMatchers("/handlerMsg/**","/favicon.ico").anonymous()
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                // swagger start
                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                // swagger end
                .antMatchers("/captcha.jpg")
                .permitAll()
                // 访问/user 需要拥有admin权限
                //  .antMatchers("/user").hasAuthority("ROLE_ADMIN")
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable();
        // 添加JWT filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // 设置UserDetailsService
                .userDetailsService(userDetailsServiceUtil)
                // 使用BCrypt进行密码的hash
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 装载BCrypt密码编码器 密码加密
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
