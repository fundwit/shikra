package com.fundwit.sys.shikra.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ServerHttpBearerAuthenticationConverter implements ServerAuthenticationConverter {
    public static final String TOKEN_TYPE = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.toLowerCase().startsWith(TOKEN_TYPE.toLowerCase())) {
            return Mono.empty();
        }

        return Mono.just(new BearerAuthenticationToken(authorization.substring(TOKEN_TYPE.length())));
    }
}
