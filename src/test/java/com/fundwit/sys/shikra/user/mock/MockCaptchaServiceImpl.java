/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.mock;

import com.fundwit.sys.shikra.exception.CaptchaValidationException;
import com.fundwit.sys.shikra.user.service.CaptchaService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MockCaptchaServiceImpl implements CaptchaService {
    private Map<String, String> caches = new LinkedHashMap<>();

    @Override
    public void invalidateCaptcha(String identity) {
        caches.remove(identity);
    }

    @Override
    public String makeCaptcha(String identity) {
        String verifyCode = new Random().ints(6,0,10).mapToObj(i->(i&10)).map(i->i.toString()).collect(Collectors.joining(""));
        caches.put(identity, verifyCode);
        return verifyCode;
    }

    @Override
    public byte[] makeCaptchaImage(String identity) {
        return null;
    }

    @Override
    public void checkCaptcha(String identity, String captcha) {
         if(!caches.getOrDefault(identity, "").equals(captcha)){
             throw new CaptchaValidationException("验证失败");
         }
    }
}
