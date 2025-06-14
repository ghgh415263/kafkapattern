package com.example.kafkapattern.common.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SagaManager<T> {

    private final SagaDefinitionProvider sagaDefinitionProvider;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;
    private final ObjectMapper objectMapper;

    public void startSaga(String sagaType, T sagaPayload) {
        SagaDefinition<T> definition = sagaDefinitionProvider.get(sagaType);
        List<String> steps = definition.getSteps();

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("SagaDefinition for type '" + sagaType + "' must have at least one step.");
        }

        UUID correlationId = UUID.randomUUID();
        SagaInstance sagaInstance = new SagaInstance(sagaType, objectMapper.valueToTree(sagaPayload), correlationId);
        log.info("Creating SagaInstance [type={}, correlationId={}]", sagaType, correlationId);

        steps.forEach(stepName -> sagaInstance.addStep(new SagaStep(sagaInstance, stepName)));

        sagaInstanceRepository.save(sagaInstance);

        String firstStep = steps.get(0);
        log.info("Starting first step '{}' for SagaInstance [{}]", firstStep, correlationId);
        executeSteps(sagaInstance, firstStep, sagaPayload, definition);
    }

    public void markStepSucceeded(UUID correlationId, T sagaPayload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new IllegalArgumentException("SagaInstance not found: " + correlationId));

        String currentStep = sagaInstance.getCurrentStep();
        sagaInstance.updateStepStatus(currentStep, SagaStepStatus.SUCCEEDED);

        SagaDefinition<T> definition = sagaDefinitionProvider.get(sagaInstance.getType());

        if (definition.isLastStep(currentStep)) {
            sagaInstance.markCompleted();
        } else {
            String nextStep = definition.getNextStep(currentStep);
            executeSteps(sagaInstance, nextStep, sagaPayload, definition);
        }
    }

    private void executeSteps(SagaInstance sagaInstance, String startStep, T sagaPayload, SagaDefinition<T> definition) {
        String currentStep = startStep;

        while (true) {
            sagaInstance.setCurrentStep(currentStep);

            StepResult result = definition.executeStep(currentStep, sagaPayload, sagaInstance.getCorrelationId());
            SagaStep sagaStep = findSagaStep(sagaInstance, currentStep);

            if (result == StepResult.COMPLETED_AND_CONTINUE) {
                sagaStep.setStatus(SagaStepStatus.SUCCEEDED);

                if (definition.isLastStep(currentStep)) {
                    sagaInstance.markCompleted();
                    break;
                }

                currentStep = definition.getNextStep(currentStep);
            } else if (result == StepResult.COMPLETED_AND_WAIT) {
                sagaStep.setStatus(SagaStepStatus.WAITING);
                break;
            } else {
                sagaStep.setStatus(SagaStepStatus.FAILED);
                sagaInstance.markFailed();
                break;
            }
        }
    }

    private SagaStep findSagaStep(SagaInstance sagaInstance, String stepName) {
        return sagaStepRepository.findBySagaInstanceAndStepName(sagaInstance, stepName)
                .orElseThrow(() -> new IllegalStateException("Step not found: " + stepName));
    }
}

