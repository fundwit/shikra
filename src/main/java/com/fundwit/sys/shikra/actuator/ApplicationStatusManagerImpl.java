package com.fundwit.sys.shikra.actuator;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ApplicationStatusManagerImpl implements ApplicationStatusManager{
    // dataSourceCheck
    // mqCheck
    // redisCheck

    @Override
    public Mono<Boolean> isAlive() {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> isReady() {
        return Mono.just(true);
    }
}
