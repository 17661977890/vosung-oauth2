package com.vosung.authentication.authrizationserverconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * 一、配置oauth2授权认证服务机制
 * @author 彬
 */
@Slf4j
@Configuration
@EnableAuthorizationServer //开启授权服务功能
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    /**
     * 认证管理器，当你选择了资源所有者密码（password）授权类型的时候，
     * 请设置这个属性注入一个 AuthenticationManager 对象
     */
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    /**
     * 通过RedisConnectionFactory工厂创建RedisConnection
     */
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    /**
     * （1）定义授权类型和令牌端点以及令牌服务：（提供授权访问端点，token的生成存储方案）
     * 配置对象有一个 pathMapping() 方法用来配置端点的 URL
     * AuthorizationEndpoint 服务于认证请求。默认 URL： /oauth/authorize。
     * TokenEndpoint 服务于访问令牌的请求。默认 URL： /oauth/token。 ---------登录时先请求这个
     *
     *  1、通过注入 AuthenticationManager 密码授权
     *  2、userDetailsService：如果你注入一个 UserDetailsService，或者全局地配置了一个UserDetailsService
     *      （例如在 GlobalAuthenticationManagerConfigurer中），那么刷新令牌授权将包含对用户详细信息的检查，以确保该帐户仍然是活动的
     *  3、authorizationCodeServices：为授权代码授权定义授权代码服务（AuthorizationCodeServices 的实例）。
     *  4、implicitGrantService：在 imlpicit 授权期间管理状态。这个属性用于设置隐式授权模式，用来管理隐式授权模式的状态
     *  5、tokenGranter：TokenGranter（完全控制授予和忽略上面的其他属性），完全自定义授权服务实现（TokenGranter 接口实现），只有当标准的四种授权模式已无法满足需求时
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter());
    }

    /**
     * （2）配置令牌端点(Token Endpoint)的安全约束.
     * tokenKeyAccess("permitAll()")：开启/oauth/token_key验证端口无权限访问
     * checkTokenAccess("isAuthenticated()")：开启/oauth/check_token验证端口认证权限访问
     *
     * /oauth/token_key(如果使用JWT，可以获的公钥用于 token 的验签)
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    /**
     * （3）配置客户端详情服务（ClientDetailsService），客户端详情信息在这里进行初始化，
     * 你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息
     * -----我们这里用的是调取数据库的方式  即通过JdbcClientDetailsService类访问oauth_client_details表
     *
     * 在oauth_client_details表中我们设置存活时间为100000秒，底层默认是12h，但会优先调取我们的设置。
     * 具体可看DefalutTokenService类
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService());
    }

    /**
     * token令牌生成转换器:
     * AuthorizationServerTokenServices 提供了对AccessToken的相关操作创建、刷新、获取
     * OAuth2就默认为我们提供了一个默认的DefaultTokenServices。包含了一些有用实现，------里面设置了默认的存活时间12H
     * 可以使用它来修改令牌的格式和令牌的存储等，但是生成的token是随机数。
     *
     * 这里使用继承使用Jwt的方式，添加额外属性，即转换器。
     * ------目前是对称密钥签署令牌
     * ------优化使用RSA非对称加密签署token
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        //使用非对称加密RSA
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("ljl-jwt.jks"),
                "ljl123".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("ljl-jwt"));
        log.info("================对token使用非对称加密RSA算法=======================");
        //使用对称加密
//        converter.setSigningKey("secret");
        return converter;
    }

    /**
     * token令牌存储器
     * 这里使用自定义redis缓存存储。
     * 也有其他多种实现方式
     * inMemoryTokenStore：默认采用该实现，将令牌信息保存在内存中，易于调试
     * JdbcTokenStore：令牌会被保存近关系型数据库，可以在不同服务器之间共享令牌
     * JwtTokenStore：使用 JWT 方式保存令牌，它不需要进行存储，但是它撤销一个已经授权令牌会非常困难，
     * 所以通常用来处理一个生命周期较短的令牌以及撤销刷新令牌,写法如下
     * return TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new CustomRedisTokenStore(redisConnectionFactory);
    }

    /**
     * 客户端详情配置注入（实现方式）
     * @return
     */
    @Bean
    public ClientDetailsService clientDetailsService() {
        return new ClientDetailsServiceImpl();
    }
}
