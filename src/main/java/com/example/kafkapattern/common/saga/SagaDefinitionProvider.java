package com.example.kafkapattern.common.saga;

public interface SagaDefinitionProvider {
    SagaDefinition get(String sagaType);
}
