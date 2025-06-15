package com.example.kafkapattern.common.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SagaManager<T> {

    private final Class<T> payloadClass;
    private final SagaDefinitionProvider sagaDefinitionProvider;
    private final SagaInstanceRepository sagaInstanceRepository;
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

    public void processNextStep(UUID correlationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new IllegalArgumentException("SagaInstance not found: " + correlationId));

        String currentStep = sagaInstance.getCurrentStep();
        sagaInstance.updateStepStatus(currentStep, SagaStepStatus.SUCCEEDED);

        SagaDefinition<T> definition = sagaDefinitionProvider.get(sagaInstance.getType());

        if (definition.isLastStep(currentStep)) {
            sagaInstance.markCompleted();
        } else {
            T sagaPayload = objectMapper.convertValue(sagaInstance.getPayload(), payloadClass);
            String nextStep = definition.getNextStep(currentStep);
            executeSteps(sagaInstance, nextStep, sagaPayload, definition);
        }
    }

    private void executeSteps(SagaInstance sagaInstance, String startStep, T sagaPayload, SagaDefinition<T> definition) {
        String currentStep = startStep;

        while (true) {
            sagaInstance.setCurrentStep(currentStep);

            StepResult result = definition.executeStep(currentStep, sagaPayload, sagaInstance.getCorrelationId());

            if (result == StepResult.COMPLETED_AND_CONTINUE) {
                sagaInstance.updateStepStatus(currentStep, SagaStepStatus.SUCCEEDED);

                if (definition.isLastStep(currentStep)) {
                    sagaInstance.markCompleted();
                    break;
                }

                currentStep = definition.getNextStep(currentStep);
            } else if (result == StepResult.COMPLETED_AND_WAIT) {
                sagaInstance.updateStepStatus(currentStep, SagaStepStatus.WAITING);
                break;
            } else {
                sagaInstance.updateStepStatus(currentStep, SagaStepStatus.FAILED);
                sagaInstance.markFailed();
                break;
            }
        }
    }


    public void processCompensation(UUID correlationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new IllegalArgumentException("SagaInstance not found: " + correlationId));

        T sagaPayload = objectMapper.convertValue(sagaInstance.getPayload(), payloadClass);
        SagaDefinition<T> definition = sagaDefinitionProvider.get(sagaInstance.getType());

        String failedStep = sagaInstance.getCurrentStep();
        sagaInstance.updateStepStatus(failedStep, SagaStepStatus.FAILED); // 실패한 스텝 상태 기록

        List<String> previousSteps = definition.getPreviousSteps(failedStep);
        ListIterator<String> iterator = previousSteps.listIterator(previousSteps.size());

        while (iterator.hasPrevious()) {
            String stepName = iterator.previous();

            if (sagaInstance.isStepSuccessful(stepName)) {
                try {
                    definition.compensateStep(stepName, sagaPayload, correlationId);
                    sagaInstance.updateStepStatus(stepName, SagaStepStatus.COMPENSATED);
                } catch (Exception e) {
                    log.error("Compensation failed for step '{}': {}", stepName, e.getMessage(), e);
                    sagaInstance.updateStepStatus(stepName, SagaStepStatus.COMPENSATION_FAILED);
                    sagaInstance.markFailed();
                    return;
                }
            }
        }

        sagaInstance.markFailed(); // 전체 Saga 실패 처리
    }
}

