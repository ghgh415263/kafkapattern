package com.example.kafkapattern.common.saga;

import java.util.List;
import java.util.UUID;

public interface SagaDefinition<T> {
    String getType();
    List<String> getSteps();
    boolean isLastStep(String stepName);
    List<String> getPreviousSteps(String stepName);
    String getNextStep(String stepName);
    StepResult executeStep(String stepName, T payload, UUID correlationId);
    void compensateStep(String stepName, T payload, UUID correlationId);
}
