package com.fundwit.sys.shikra.actuator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("build")
public class BuildInfoProperties {
    public static final String VERSION_UNKNOWN = "unknown";

    private String version = VERSION_UNKNOWN;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}
