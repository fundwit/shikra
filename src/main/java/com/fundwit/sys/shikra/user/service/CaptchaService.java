/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;

public interface CaptchaService {
    void invalidateCaptcha(String identity);
    String makeCaptcha(String identity);
    byte[] makeCaptchaImage(String identity);
    void checkCaptcha(String identity, String captcha);
}
