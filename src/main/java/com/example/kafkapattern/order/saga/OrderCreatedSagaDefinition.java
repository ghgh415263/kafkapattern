package com.example.kafkapattern.order.saga;

import com.example.kafkapattern.common.saga.SagaDefinition;
import com.example.kafkapattern.common.saga.StepResult;
import com.example.kafkapattern.order.event.CommandPublisher;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderCreatedSagaDefinition implements SagaDefinition<OrderCreateSagaPayload> {

    private final CommandPublisher commandPublisher;

    @Override
    public String getType() {
        return "OrderCreatedSaga";
    }

    @Override
    public List<String> getSteps() {
        return List.of("CreateOrder", "ProcessPayment", "ApproveOrder");
    }

    @Override
    public boolean isLastStep(String stepName) {
        List<String> steps = getSteps();
        int index = steps.indexOf(stepName);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown step: " + stepName);
        }
        return index == steps.size() - 1;
    }

    @Override
    public String getNextStep(String stepName) {
        List<String> steps = getSteps();
        int index = steps.indexOf(stepName);

        if (index == -1) {
            throw new IllegalArgumentException("Unknown step name: " + stepName);
        }
        if (index == steps.size() - 1) {
            throw new IllegalStateException("Step '" + stepName + "' is the last step; no next step exists.");
        }
        return steps.get(index + 1);
    }

    @Override
    public List<String> getPreviousSteps(String stepName) {
        List<String> steps = getSteps();
        int index = steps.indexOf(stepName);
        if (index == -1) {
            throw new IllegalArgumentException("Step not found: " + stepName);
        }
        return steps.subList(0, index);
    }

    @Override
    public StepResult executeStep(String stepName, OrderCreateSagaPayload payload, UUID correlationId) {
        switch (stepName) {
            case "CreateOrder" -> {
                return StepResult.COMPLETED_AND_CONTINUE;
            }
            case "ProcessPayment" -> {
                callPaymentService(payload, correlationId);
                return StepResult.COMPLETED_AND_WAIT;
            }
            case "ApproveOrder" -> {
                approveOrder(payload, correlationId);
                return StepResult.COMPLETED_AND_CONTINUE;
            }
            default -> throw new IllegalArgumentException("Unknown execute step: " + stepName);
        }
    }

    private void callPaymentService(OrderCreateSagaPayload payload, UUID correlationId) {
        PaymentProcessCommand command = new PaymentProcessCommand(
                payload.orderId(),
                payload.userId(),
                payload.paymentMethodId(),
                payload.amount()
        );

        commandPublisher.publish(
                "payment-service-command",
                PaymentProcessCommand.class.getSimpleName(),
                payload.orderId().toString(),
                command,
                correlationId
        );
    }

    private void approveOrder(OrderCreateSagaPayload payload, UUID correlationId) {
        // 주문 상태를 최종적으로 COMPLETED 로 변경하는 로직 (예: orderService.complete(payload.orderId()))
        // 외부 알림 발송 등을 여기에 포함할 수도 있음
    }

    @Override
    public void compensateStep(String stepName, OrderCreateSagaPayload payload, UUID correlationId) {
        switch (stepName) {
            case "ApproveOrder" -> {}
            case "ProcessPayment" -> cancelPayment(payload, correlationId);
            case "CreateOrder" -> cancelOrder(payload, correlationId);
            default -> throw new IllegalArgumentException("Unknown compensation step: " + stepName);
        }
    }

    private void cancelPayment(OrderCreateSagaPayload payload, UUID correlationId) {
        // 결제 취소 커맨드 발행 또는 로직 실행
        // 예: paymentService.cancel(payload.orderId());
    }

    private void cancelOrder(OrderCreateSagaPayload payload, UUID correlationId) {
        // 주문 삭제 또는 상태를 CANCELLED 로 변경
        // 예: orderService.cancel(payload.orderId());
    }
}