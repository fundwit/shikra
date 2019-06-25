package com.fundwit.sys.shikra.authentication;


import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

public class JwtTokenSignAuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        webFilterExchange.getExchange().getResponse().beforeCommit(()->{
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            response.getHeaders().add("user", authentication.getName());

            // 生成 JWT
            // 生成 Cookie


            return Mono.empty();
        });
        ReactiveSecurityContextHolder.withAuthentication(authentication);
        return super.onAuthenticationSuccess(webFilterExchange, authentication);
    }
}
