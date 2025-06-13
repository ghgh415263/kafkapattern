package com.example.kafkapattern.common.saga;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface SagaDefinition {
    String getType();
    List<String> getSteps();
    void executeStep(String stepName, JsonNode payload);
    void compensateStep(String stepName, JsonNode payload);
}
