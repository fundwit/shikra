package com.fundwit.sys.shikra.actuator;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("build")
@Api("build info")
public class BuildInfoController {
    @Autowired
    private BuildInfoProperties buildInfoProperties;

    @GetMapping
    public Mono<BuildInfoProperties> liveness(){
        return Mono.just(buildInfoProperties);
    }
}
