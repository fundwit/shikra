package com.fundwit.sys.shikra.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@ConfigurationProperties("jwt")
public class JwtServiceProperties {
    private Duration expiration = Duration.ofMinutes(30);

    public Duration getExpiration() {
        return expiration;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }
}
