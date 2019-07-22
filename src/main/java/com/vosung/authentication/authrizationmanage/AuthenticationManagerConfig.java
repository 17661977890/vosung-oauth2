package com.vosung.authentication.authrizationmanage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

/**
 * 一、提供登陆验证方法：
 * 主要的验证方法authenticate(Authentication authentication)在接口AuthenticationManager中，
 * 其实现类有ProviderManager，而ProviderManager又依赖于AuthenticationProvider接口，其定义了一个List<AuthenticationProvider>全局变量。
 * 我们这边定义了实现了该接口的实现类CustomAuthenticationProvider。自定义一个provider
 * 并在GlobalAuthenticationConfigurerAdapter中配置好改自定义的校验provider，覆写configure()方法。
 * @author keets
 * @date 2017/9/25
 */
@Configuration
public class AuthenticationManagerConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    /**
     * AuthenticationManagerBuilder是用来创建AuthenticationManager，
     * 允许自定义提供多种方式的AuthenticationProvider
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

}