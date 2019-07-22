package com.vosung.authentication.authrizationmanage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 认证管理：（影响授权类型之一）
 * 全局地配置了一个UserDetailsService（例如在 GlobalAuthenticationManagerConfigurer中），
 * 那么刷新令牌授权将包含对用户详细信息的检查，以确保该帐户仍然是活动的
 * @author keets
 * @date 2017/8/5
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;


    /**
     * 重写登录验证方法
     * filter执行判断的入口--->后面再去执行check-token过程，在资源服务配置那边的loadAuthentication方法
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        Map data;
        if(authentication.getDetails() instanceof Map) {
            data = (Map) authentication.getDetails();
        }else{
            return null;
        }
        String clientId = (String) data.get("client");
        Assert.hasText(clientId, "clientId must have value");

        String password = (String) authentication.getCredentials();

        CustomUserDetails customUserDetails = checkUsernameAndPassword(username, password);
        if (customUserDetails == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        customUserDetails.setClientId(clientId);
        return new CustomAuthenticationToken(customUserDetails);
    }

    /**
     * 检查用户名和密码（密码加密后相同）
     * @param username
     * @param password
     * @return
     */
    private CustomUserDetails checkUsernameAndPassword(String username, String password) {
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        String pwd = setSHAPassword(password);
        if (userDetails == null || !userDetails.getPassword().equals(pwd)) {
            return null;
        }
        return userDetails;
    }

    public String setSHAPassword(String password) {
        MessageDigest messageDigest;
        String SHAPwd = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((password).getBytes("UTF-8"));
            SHAPwd = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "vosung"+SHAPwd+"authapp";
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    public String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for(int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if(temp.length()==1){
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}