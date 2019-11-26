/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.authentication.LoginUser;
import com.fundwit.sys.shikra.user.service.UserServiceImpl;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@RequestMapping
public class AuthController {
    public static final String AUTH_LOGIN_ENDPOINT_PATH = "/auth/login";

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ObjectProvider<ServerAuthenticationConverter> convertersProviders;

    @Value("${auth.form.endpoint:}")
    private String authFormEndpoint;

    @GetMapping(AUTH_LOGIN_ENDPOINT_PATH)
    public Mono<ResponseEntity<Void>> goFormLogin(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, authFormEndpoint).build());
    }

    @PostMapping(AUTH_LOGIN_ENDPOINT_PATH)
    public Mono<ResponseEntity<LoginUser>> doLogin(ServerWebExchange exchange){
        return Flux.fromStream(convertersProviders.orderedStream())
                .flatMap(converter->converter.convert(exchange))
                .filter(authentication -> authentication instanceof UsernamePasswordAuthenticationToken)
                .elementAt(0)
                .map(authentication -> (UsernamePasswordAuthenticationToken) authentication)
                .flatMap(authentication -> userService.authenticate(authentication.getPrincipal().toString(), authentication.getCredentials().toString()))
                .map(loginUser -> ResponseEntity.ok(loginUser));
    }
}
