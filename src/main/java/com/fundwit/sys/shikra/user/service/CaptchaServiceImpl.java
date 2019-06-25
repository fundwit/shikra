/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;

import com.fundwit.sys.shikra.exception.CaptchaValidationException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;

import java.util.Random;
import java.util.stream.Collectors;

public class CaptchaServiceImpl implements CaptchaService {
    private static final String CAPTCHA_CACHE = "CAPTCHA_CACHE";
    private CacheManager cacheManager;

    public CaptchaServiceImpl(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @Override
    public void invalidateCaptcha(String identity) {
        Cache cache = cacheManager.getCache(CAPTCHA_CACHE);
        if(cache != null){
            cache.evict(identity);
        }
    }

    @Override
    @CachePut(CAPTCHA_CACHE)
    public String makeCaptcha(String identity) {
        return new Random().ints(6,0,10).mapToObj(i->(i&10)).map(i->i.toString()).collect(Collectors.joining(""));
    }

    @Override
    public byte[] makeCaptchaImage(String identity) {
        return null;
    }

    @Override
    public void checkCaptcha(String identity, String captcha) {
        Cache cache = cacheManager.getCache(CAPTCHA_CACHE);
        if(cache != null){
            String cachedCaptcha = cache.get(identity, String.class);
            if( cachedCaptcha != null && cachedCaptcha.equals(captcha)) {
                cache.evict(identity);
                return ;
            }
        }
        throw new CaptchaValidationException("验证失败");
    }

    // @Cacheable(CAPTCHA_CACHE)
    public String findCaptcha(String identity){
        Cache cache = cacheManager.getCache(CAPTCHA_CACHE);
        if(cache != null){
            return cacheManager.getCache(CAPTCHA_CACHE).get(identity, String.class);
        }
        return null;
    }
}
