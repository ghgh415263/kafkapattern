package com.example.kafkapattern.common.saga;

import com.example.kafkapattern.order.event.CommandPublisher;
import com.example.kafkapattern.order.saga.OrderCreatedSagaDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfiguration {

    @Bean
    public SagaDefinitionProvider sagaDefinitionProvider(CommandPublisher commandPublisher) {
        SagaDefinitionRegistry registry = new SagaDefinitionRegistry();

        // 필요한 사가 정의를 직접 생성해서 등록
        registry.register(new OrderCreatedSagaDefinition(commandPublisher));

        return registry;
    }
}
