package com.example.kafkapattern.common.saga;

public enum SagaStepStatus {
    WAITING,
    FAILED,
    SUCCEEDED,
    COMPENSATED,
    COMPENSATION_FAILED;
}
