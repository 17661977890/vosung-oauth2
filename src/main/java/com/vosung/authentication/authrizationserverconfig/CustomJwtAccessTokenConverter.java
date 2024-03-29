package com.vosung.authentication.authrizationserverconfig;

import com.vosung.authentication.authrizationmanage.CustomUserDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * 给token设置额外信息（拓展）：
 * JwtAccessTokenConverter是用来给token添加附加信息的的转换器
 * @author 彬
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    //自定义token时效长度
    private static int authenticateCodeExpiresTime = 10 * 60;

    private static final String TOKEN_SEG_USER_ID = "X-AOHO-UserId";
    private static final String TOKEN_SEG_CLIENT = "X-AOHO-ClientId";

    /**
     * token 生成器（添加参数）
     * @param accessToken
     * @param authentication
     * @return
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getUserAuthentication().getPrincipal();

        authentication.getUserAuthentication().getPrincipal();
        //将登陆用户id和角色代码放入token中
        Map<String, Object> info = new HashMap<String, Object>(2) {{
            put(TOKEN_SEG_USER_ID, userDetails.getUserId());
            put("roles", StringUtils.join(userDetails.getRoles(), ","));
        }};
        //DefaultOAuth2AccessToken设置token的额外属性，这里还可以设置token的时效长度等
        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(info);
        OAuth2AccessToken enhancedToken = super.enhance(customAccessToken, authentication);
        //client_id客户端id也添加进来
        enhancedToken.getAdditionalInformation().put(TOKEN_SEG_CLIENT, userDetails.getClientId());
        return enhancedToken;
    }

}