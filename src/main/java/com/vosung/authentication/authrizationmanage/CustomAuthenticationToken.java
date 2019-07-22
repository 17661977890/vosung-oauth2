package com.vosung.authentication.authrizationmanage;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 确定用户验证通过
 * Created by keets on 2017/8/5.
 */
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private CustomUserDetails userDetails;

    public CustomAuthenticationToken(CustomUserDetails userDetails) {
        super(null);
        this.userDetails = userDetails;
        //设置isAuthenticated 值为true，表示用户验证已通过。
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.userDetails;
    }

    @Override
    public Object getCredentials() {
        return this.userDetails.getPassword();
    }

}