package com.example.kafkapattern.common.saga;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SagaDefinitionRegistry implements SagaDefinitionProvider{

    private final Map<String, SagaDefinition> registry = new HashMap<>();

    public void register(SagaDefinition definition) {
        String type = definition.getType();
        if (registry.containsKey(type)) {
            throw new IllegalStateException("SagaDefinition already registered for type: " + type);
        }
        registry.put(type, definition);
    }

    public void registerAll(List<SagaDefinition> definitions) {
        for (SagaDefinition definition : definitions) {
            register(definition);
        }
    }

    @Override
    public SagaDefinition get(String sagaType) {
        SagaDefinition definition = registry.get(sagaType);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown saga type: " + sagaType);
        }
        return definition;
    }
}
