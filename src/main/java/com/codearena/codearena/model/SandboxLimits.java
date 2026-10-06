package com.codearena.codearena.model;

public class SandboxLimits {

    private final long timeoutMillis;
    private final long memoryLimitMb;

    public SandboxLimits(
            long timeoutMillis,
            long memoryLimitMb) {

        this.timeoutMillis = timeoutMillis;
        this.memoryLimitMb = memoryLimitMb;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public long getMemoryLimitMb() {
        return memoryLimitMb;
    }
}