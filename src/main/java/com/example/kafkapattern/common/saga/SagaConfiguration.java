package com.example.kafkapattern.common.saga;

import com.example.kafkapattern.order.event.CommandPublisher;
import com.example.kafkapattern.order.saga.OrderCreateSagaPayload;
import com.example.kafkapattern.order.saga.OrderCreatedSagaDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfiguration {

    @Bean
    public SagaDefinitionProvider sagaDefinitionProvider(CommandPublisher commandPublisher) {
        SagaDefinitionRegistry registry = new SagaDefinitionRegistry();

        registry.register(new OrderCreatedSagaDefinition(commandPublisher));

        return registry;
    }

    @Bean
    public SagaManager<OrderCreateSagaPayload> orderSagaManager(
            SagaDefinitionProvider sagaDefinitionProvider,
            SagaInstanceRepository sagaInstanceRepository,
            ObjectMapper objectMapper
    ) {
        return new SagaManager<>(
                OrderCreateSagaPayload.class,
                sagaDefinitionProvider,
                sagaInstanceRepository,
                objectMapper
        );
    }
}
