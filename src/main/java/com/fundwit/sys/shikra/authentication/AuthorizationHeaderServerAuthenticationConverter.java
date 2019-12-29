package com.fundwit.sys.shikra.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthorizationHeaderServerAuthenticationConverter implements ServerAuthenticationConverter {
    private ServerAuthenticationConverter basicAuthenticationConverter = new ServerHttpBasicAuthenticationConverter();
    private ServerAuthenticationConverter bearerAuthenticationConverter = new ServerHttpBearerAuthenticationConverter();

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        if(RequestHelper.isBasicAuthorization(exchange.getRequest())) {
            return basicAuthenticationConverter.convert(exchange);
        }else if(RequestHelper.isBearerAuthorization(exchange.getRequest())) {
            return bearerAuthenticationConverter.convert(exchange);
        }
        return null;
    }
}
