/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.user.service.CaptchaService;
import com.fundwit.sys.shikra.user.service.EmailService;
import com.fundwit.sys.shikra.user.service.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotBlank;

@Controller
@RequestMapping("verifier")
@Api("Verifier API")
public class VerifierController {

    private EmailService emailService;
    private CaptchaService captchaService;
    private UserServiceImpl userService;

    public VerifierController(EmailService emailService, CaptchaService captchaService, UserServiceImpl userService) {
        this.emailService = emailService;
        this.captchaService = captchaService;
        this.userService = userService;
    }

    @PostMapping("email")
    @ApiOperation(value = "send verify code to email")
    public ResponseEntity<?> emailVerifyCode(@NotBlank String email) {
        String verifyCode = captchaService.makeCaptcha(email);
        emailService.sendEmail(email, "[Shikra] 验证码", "您的验证码为："+verifyCode+", 有效期为10分钟，请尽快验证！");
        return ResponseEntity.noContent().build();
    }
}
