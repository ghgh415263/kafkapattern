package com.example.kafkapattern.common.saga;

public enum SagaStatus {
    STARTED,
    ABORTING,
    FAILED,
    COMPLETED;

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isAborted() {
        return this == FAILED;
    }
}