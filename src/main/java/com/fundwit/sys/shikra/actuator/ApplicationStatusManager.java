package com.fundwit.sys.shikra.actuator;

import reactor.core.publisher.Mono;

public interface ApplicationStatusManager {
    Mono<Boolean> isAlive(); // auto finish when not alive?
    Mono<Boolean> isReady();
}
