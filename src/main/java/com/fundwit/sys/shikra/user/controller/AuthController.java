/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class AuthController {
    @Value("${auth.form.endpoint:localhost:8081}")
    private String formAuthEndpoint;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/auth/login"),
                req-> ServerResponse.temporaryRedirect(URI.create((formAuthEndpoint.toLowerCase().startsWith("http")?"":"http://")+formAuthEndpoint)).build());
    }
}
