package com.petspa.backend.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.zalopay")
public class ZaloPayConfig {
    private String appId;
    private String key1;
    private String key2;
    private String endpoint;

    // Getters and Setters
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getKey1() { return key1; }
    public void setKey1(String key1) { this.key1 = key1; }

    public String getKey2() { return key2; }
    public void setKey2(String key2) { this.key2 = key2; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
}
