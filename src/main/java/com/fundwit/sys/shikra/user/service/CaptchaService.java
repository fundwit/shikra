/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;

public interface CaptchaService {
    String makeCaptcha(String principal);
    byte[] makeCaptchaImage(String principal);
    void checkCaptcha(String principal, String captcha);
}
