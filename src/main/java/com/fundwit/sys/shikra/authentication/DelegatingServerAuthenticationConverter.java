package com.fundwit.sys.shikra.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DelegatingServerAuthenticationConverter implements ServerAuthenticationConverter {

    private List<ServerAuthenticationConverter> serverAuthenticationConverters;

    public DelegatingServerAuthenticationConverter(List<ServerAuthenticationConverter> serverAuthenticationConverters) {
        this.serverAuthenticationConverters = serverAuthenticationConverters;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        if(serverAuthenticationConverters == null){
            return Mono.empty();
        }

        for(ServerAuthenticationConverter serverAuthenticationConverter: serverAuthenticationConverters) {
            Mono<Authentication> authentication = serverAuthenticationConverter.convert(exchange);
            Authentication auth = authentication.block();
            if(auth != null) {
                return Mono.just(auth);
            }
        }
        return Mono.empty();
    }
}
