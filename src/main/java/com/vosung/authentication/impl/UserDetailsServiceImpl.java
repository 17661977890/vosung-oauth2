package com.vosung.authentication.impl;

import com.vosung.authentication.authrizationmanage.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @description:
 **/
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    JdbcUserDetailsService jdbcUserDetailsService;

    /**
     * 根据用户名获取登录用户信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails customUserDetails = jdbcUserDetailsService.loadUserDetailsByUserName(username);
        if(customUserDetails == null){
            throw new UsernameNotFoundException("用户名："+ username + "不存在！");
        }
        //封装角色信息
        customUserDetails.setRoles(jdbcUserDetailsService.loadUserRolesByUserId(Long.valueOf(customUserDetails.getUserId())));
        //优化--将用户权限信息也封装到对应集合属性中
//        List<GrantedAuthority> authorities = new ArrayList();


        return customUserDetails;
    }
}
