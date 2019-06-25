/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthController {
    public static final String AUTH_LOGIN_ENDPOINT_PATH = "/auth/login";

    @Value("${auth.form.endpoint:localhost:8081}")
    private String formAuthEndpoint;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/auth/login"),
                req-> ServerResponse.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, (formAuthEndpoint.toLowerCase().startsWith("http")?"":"http://")+formAuthEndpoint)
                        .build());
    }
}
