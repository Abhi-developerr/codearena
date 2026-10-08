package com.codearena.codearena.model;

public class SandboxLimits {

    private final long timeoutMillis;
    private final long memoryLimitMb;

    public SandboxLimits(
        long timeoutMillis,
        long memoryLimitMb) {

    if (timeoutMillis <= 0) {
        throw new IllegalArgumentException(
                "Sandbox timeout must be greater than zero"
        );
    }

    if (memoryLimitMb <= 0) {
        throw new IllegalArgumentException(
                "Sandbox memory limit must be greater than zero"
        );
    }

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