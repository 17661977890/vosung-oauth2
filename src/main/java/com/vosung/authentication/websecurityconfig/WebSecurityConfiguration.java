package com.vosung.authentication.websecurityconfig;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * WebSecurityConfigurerAdapter是默认情况下spring security的http配置：
 * 在ResourceServerProperties中，定义了它的order默认值为SecurityProperties.ACCESS_OVERRIDE_ORDER - 1;，
 * 是大于100的,即WebSecurityConfigurerAdapter的配置的拦截要优先于ResourceServerConfigurerAdapter，
 * 优先级高的http配置是可以覆盖优先级低的配置的。
 * 某些情况下如果需要ResourceServerConfigurerAdapter的拦截优先于WebSecurityConfigurerAdapter
 *
 * 优先级高于ResourceServerConfigurer，用于保护oauth相关的endpoints，同时主要作用于用户的登录（form login，Basic auth）
 *
 * 需要在配置文件中添加 security.oauth2.resource.filter-order=99 或者重写WebSecurityConfigurerAdapter的Order配置
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * （1）用户认证管理器 authmanage包里面已经实现
     * 通过userServiceDetail类 来获取用户数据
     */

    /**
     * （2）设置获取token的url，即请求授权
     * 使用匹配器匹配路径:
     *  authorizeRequests()方法：开始请求权限配置，即定义那些url需要被保护，哪些不需要
     *  antMatchers:使用ant风格的路径进行匹配
     *  regexMatchers：使用正则表达式匹配路径
     *  anyRequest:匹配所有请求路径
     *  csrf().disable()：关闭csrf，防止请求出现csrf跨站请求伪造。
     *  authenticated()，为用户登陆后即可访问。
     *  permitAll(),不需要登录即可访问
     *  httpBasic()：Http Basic认证方式
     *      流程：1.浏览器发送get请求到服务器，服务器检查是否含有请求头Authorization信息，
     *          若没有则返回响应码401且加上响应头
     *          2.浏览器得到响应码自动弹出框让用户输入用户名和密码，
     *          浏览器将用户名和密码进行base64编码（用户名：密码），设置请求头Authorization，继续访问
     * OPTIONS方法是用于请求获得由Request-URI标识的资源在请求/响应的通信过程中可以使用的功能选项。
     * 通过这个方法，客户端可以在采取具体资源请求之前，决定对该资源采取何种必要措施，或者了解服务器的性能。
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest().authenticated().and()
                .httpBasic().and().csrf().disable();
    }

    /**
     * 密码加密器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}