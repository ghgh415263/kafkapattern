package com.example.kafkapattern.common.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaManager {

    private final SagaDefinitionProvider sagaDefinitionProvider;

    private final SagaInstanceRepository sagaInstanceRepository;

    private final SagaStepRepository sagaStepRepository;




}
