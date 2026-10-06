package com.codearena.codearena.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sandbox")
public class SandboxProperties {

    private long timeoutMillis;
    private long memoryLimitMb;

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public long getMemoryLimitMb() {
        return memoryLimitMb;
    }

    public void setMemoryLimitMb(long memoryLimitMb) {
        this.memoryLimitMb = memoryLimitMb;
    }
}