package com.fundwit.sys.shikra.authentication;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequiresAuthenticationMatcher implements ServerWebExchangeMatcher {
    private String authLoginPath;

    public RequiresAuthenticationMatcher(String authLoginPath) {
        this.authLoginPath = authLoginPath;
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return (RequestHelper.isPostLoginPath(request, authLoginPath) || RequestHelper.isWithAuthorization(request)) ?
                 ServerWebExchangeMatcher.MatchResult.match() : ServerWebExchangeMatcher.MatchResult.notMatch();
    }

    public String getAuthLoginPath() {
        return authLoginPath;
    }
}
