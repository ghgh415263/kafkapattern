package com.example.kafkapattern.common.saga;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "saga_type", nullable = false, length = 100)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "json")
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", nullable = false)
    private SagaStatus sagaStatus;

    @Column(name = "current_step")
    private String currentStep;

    @Column(name = "correlation_id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID correlationId;

    @OneToMany(mappedBy = "sagaInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SagaStep> steps = new ArrayList<>();

    public void addStep(SagaStep step) {
        step.setSagaInstance(this);
        this.steps.add(step);
    }

    // 기본 생성자 유지 (필요시)
    public SagaInstance(String sagaType, JsonNode payload, UUID correlationId) {
        this.type = sagaType;
        this.payload = payload;
        this.correlationId = correlationId;
        this.sagaStatus = SagaStatus.STARTED;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public void markCompleted() {
        this.sagaStatus = SagaStatus.COMPLETED;
    }

    public void markFailed() {
        this.sagaStatus = SagaStatus.FAILED;
    }

    public boolean isStepSuccessful(String stepName) {
        return steps.stream()
                .filter(step -> step.getStepName().equals(stepName))
                .findFirst()
                .map(step -> step.getStatus() == SagaStepStatus.SUCCEEDED)
                .orElse(false);
    }

    public void updateStepStatus(String stepName, SagaStepStatus newStatus) {
        for (SagaStep step : steps) {
            if (step.getStepName().equals(stepName)) {
                step.setStatus(newStatus);
                return;
            }
        }
        throw new IllegalArgumentException("Step not found: " + stepName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SagaInstance that = (SagaInstance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SagaInstance{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", sagaStatus=" + sagaStatus +
                '}';
    }
}

