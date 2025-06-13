package com.example.kafkapattern.order.saga;

import com.example.kafkapattern.common.saga.SagaDefinition;
import com.example.kafkapattern.order.event.CommandPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderCreatedSagaDefinition implements SagaDefinition {

    private final CommandPublisher commandPublisher;

    @Override
    public String getType() {
        return "OrderCreatedSaga";
    }

    @Override
    public List<String> getSteps() {
        return List.of("CreateOrder", "ProcessPayment");
    }

    @Override
    public void executeStep(String stepName, JsonNode payload) {
        switch (stepName) {
            case "ProcessPayment" -> callPaymentService(payload);
            default -> throw new IllegalArgumentException("Unknown execute step: " + stepName);
        }
    }

    private void callPaymentService(JsonNode payload) {
    }

    @Override
    public void compensateStep(String stepName, JsonNode payload) {
        switch (stepName) {
            case "ProcessPayment" -> cancelPayment(payload);
            case "CreateOrder" -> cancelOrder(payload);
            default -> throw new IllegalArgumentException("Unknown compensation step: " + stepName);
        }
    }

    private void cancelPayment(JsonNode payload) {
    }

    private void cancelOrder(JsonNode payload) {
    }
}