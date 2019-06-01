package com.fundwit.sys.shikra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class ShikraApplication {
    public static void main(String[] args) {
        // SpringApplication.run(ShikraApplication.class, args);

        SpringApplication application = new SpringApplication(new Class<?>[] { ShikraApplication.class });
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }
}
