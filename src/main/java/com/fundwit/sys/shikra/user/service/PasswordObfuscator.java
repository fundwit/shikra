package com.fundwit.sys.shikra.user.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordObfuscator {
    public String obfuscate(String password, String salt) {
        return salt+password;
    }
}
