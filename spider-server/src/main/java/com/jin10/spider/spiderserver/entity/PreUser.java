package com.jin10.spider.spiderserver.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author Airey
 * @date 2019/11/11 11:52
 * ----------------------------------------------
 * 用户身份权限认证类 登陆身份认证
 * ----------------------------------------------
 */
@Setter
@Getter
@Accessors(chain = true)
public class PreUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private Integer status;
    private Collection<? extends GrantedAuthority> authorities;


    public PreUser(Long userId, String username, String password, Integer status, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
    }

    public PreUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }


    /**
     * 返回分配给用户的角色列表
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    /**
     * 账户是否过期，过期无法验证
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁，锁定的用户无法进行身份验证
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用,禁用的用户不能身份认证
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}
