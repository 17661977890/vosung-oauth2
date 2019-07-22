package com.vosung.authentication.authrizationmanage;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetails是Spring Security中一个核心的接口。
 * 其中定义了一些可以获取用户名、密码、权限等与认证相关的信息的方法。
 * @author 彬
 */
@Data
public class CustomUserDetails implements UserDetails {

    static final long serialVersionUID = -7588980448693010399L;

    private String userId;

    private String username;

    private String password;

    private boolean enabled = true;

    private String clientId;

    private List<Integer> roles;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 获取登录用户的权限
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取密码
     * @return
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 获取用户名
     * @return
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 用户账号是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户账号是否被锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户密码是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否可用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static class CustomUserDetailsBuilder {
        private CustomUserDetails userDetails = new CustomUserDetails();

        public CustomUserDetailsBuilder withUsername(String username) {
            userDetails.setUsername(username);
            userDetails.setAuthorities(null);
            return this;
        }

        public CustomUserDetailsBuilder withPassword(String password) {
            userDetails.setPassword(password);
            return this;
        }

        public CustomUserDetailsBuilder withClientId(String clientId) {
            userDetails.setClientId(clientId);
            return this;
        }

        public CustomUserDetailsBuilder withUserId(String userId) {
            userDetails.setUserId(userId);
            return this;
        }

        public CustomUserDetailsBuilder withRoles(List<Integer> roles) {
            userDetails.setRoles(roles);
            return this;
        }

        public CustomUserDetails build() {
            return userDetails;
        }
    }

}