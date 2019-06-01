/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.service.CaptchaService;
import com.fundwit.sys.shikra.user.service.UserServiceImpl;
import com.google.common.net.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("register")
@Api("Register API")
public class RegisterController {

    private UserServiceImpl userService;
    private CaptchaService captchaService;

    public RegisterController(UserServiceImpl userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    @ApiOperation(value = "user register (json)", notes="register by json request")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> registerJson(@RequestBody @NotNull @Validated RegisterRequest registerRequest) {
        captchaService.checkCaptcha(registerRequest.getEmail(),registerRequest.getVerifyCode());
        Mono<User> user = userService.createUser(registerRequest);
        return user.map(u-> {
            u.setSalt(null);
            return u;
        });
    }

    @PostMapping
    @ResponseBody
    @ApiOperation(value = "user register (form)", notes="register by form request")
    public Mono<ResponseEntity<?>> registerHtml(
            @NotNull @Validated RegisterRequest registerRequest,
            @RequestParam(value = "url", defaultValue = "/") String callbackUrl) {

        captchaService.checkCaptcha(registerRequest.getEmail(),registerRequest.getVerifyCode());
        Mono<User> user = userService.createUser(registerRequest);
        return user.map(u-> ResponseEntity.status(HttpStatus.FOUND.value()).header(HttpHeaders.LOCATION, callbackUrl).build());
    }

    @PostMapping("usernames")
    @ResponseBody
    @ApiOperation(value = "user username exists", notes="check user username exists")
    public ResponseEntity<?> nameExists(@NotBlank @RequestParam("query") String username) {
        if(userService.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("emails")
    @ResponseBody
    @ApiOperation(value = "check user email exists", notes="check user email exists")
    public ResponseEntity<?> emailExists(@NotBlank @RequestParam("query") String email) {
        if(userService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            return ResponseEntity.noContent().build();
        }
    }
}
