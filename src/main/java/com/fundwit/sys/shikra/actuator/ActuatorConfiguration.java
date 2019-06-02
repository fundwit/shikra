package com.fundwit.sys.shikra.actuator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class ActuatorConfiguration {
    @ConditionalOnResource(resources={"file:buildInfo.properties"})
    @PropertySource({"file:buildInfo.properties"})
    public static class FileBuildInfoProperties {
        @Bean
        @Primary
        public BuildInfoProperties buildProperties(){
            return new BuildInfoProperties();
        }
    }

    @Bean
    public BuildInfoProperties unknownBuildProperties(){
        return new BuildInfoProperties();
    }
}
