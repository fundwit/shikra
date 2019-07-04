package com.fundwit.sys.shikra.authentication.token;

import org.springframework.web.server.ServerWebExchange;

public interface JwtTokenRepository {
    String load(ServerWebExchange exchange);
    void save(ServerWebExchange exchange);
}
