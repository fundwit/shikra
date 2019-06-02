package com.fundwit.sys.shikra.actuator;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("health")
@Api("health check API")
public class HealthCheckController {
    @Autowired
    private ApplicationStatusManager statusManager;

    @GetMapping("/liveness")
    public Mono<Boolean> liveness(){
        return statusManager.isAlive();
    }
    @GetMapping("/readiness")
    public Mono<Boolean> readiness(){
        return statusManager.isReady();
    }
}
