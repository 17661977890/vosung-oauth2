package com.vosung.authentication.websecurityconfig;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 加密器
 */
public class MyPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence arg0) {
        return arg0.toString();
    }

    @Override
    public boolean matches(CharSequence arg0, String arg1) {
        return arg1.equals(arg0.toString());
    }
}
