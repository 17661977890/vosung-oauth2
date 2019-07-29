# oauth2
## 授权认证服务器

* 授权认证中心：vosung-auth-server，此项目结合了spring cloud oauth2的认证服务器功能，
* 实现了派发token的作用，同时此认证中心纳入到了服务注册中心，实现高可用。。

* 配置认证服务器：AuthorizationServerConfiguration

#### 用户信息封装token的全过程：

* 现在的授权认证服务器--在CustomJwtAccessTokenConverter token生成转换器中，将已获取封装的用户对象CustomUserDetails部分信息放入token
* 主要是里面的用户id 和 roles  如果想加入其他用户信息，首先看CustomUserDetails 这个对象是否设置了相关用户信息，如果没有，就要新增属性并设值。
* 封装CustomUserDetails 类信息的地方是：UserDetailsServiceImpl  （可以一并将权限list塞入）
* 在CustomAuthenticationProvider认证管理处：将用户信息CustomUserDetails 转化为Authentication对象 保存
* 在CustomJwtAccessTokenConverter token生成转换处理器：从Authentication对象 取出所需要的用户对象塞入token的additionalInformation map对象中。

#### 注意事项：
* 当我们请求  服务器ip:端口/login/oauth/token 参数：client（不同客户端的id） grant_type（授权验证方式：password） password（用户密码） username（用户名）
* 因为配置了网关，所以请求网关的端口，由网关路由转发到授权认证服务器进行用户的认证授权。





* 授权流程:
![image](https://github.com/17661977890/vosung-oauth2/blob/master/src/main/resources/%E6%8E%88%E6%9D%83%E6%B5%81%E7%A8%8B.png)
