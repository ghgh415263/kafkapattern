package com.example.kafkapattern.common.saga;

public enum SagaStepStatus {
    WAITING,
    FAILED,
    SUCCEEDED,
    COMPENSATING,
    COMPENSATED;

    public boolean isSucceeded() {
        return SUCCEEDED == this;
    }

    public boolean isFailedOrCompensated() {
        return this == FAILED || this == COMPENSATED;
    }

}
