package com.example.kafkapattern.common.saga;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class SagaInstance {

    @Id
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

    @OneToMany(mappedBy = "sagaInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SagaStep> steps = new HashSet<>();

    public SagaInstance(String sagaType, JsonNode payload) {
        this.id = UUID.randomUUID();
        this.type = sagaType;
        this.payload = payload;
        this.sagaStatus = SagaStatus.STARTED;
    }

    public void addStep(SagaStep step) {
        this.steps.add(step);
        step.setSagaInstance(this);
    }

    public void updateSagaStatus() {
        boolean allSucceeded = steps.stream().allMatch(s -> s.getStatus() == SagaStepStatus.SUCCEEDED);
        boolean anyFailed = steps.stream().anyMatch(s -> s.getStatus() == SagaStepStatus.FAILED);
        boolean anyCompensating = steps.stream().anyMatch(s -> s.getStatus() == SagaStepStatus.COMPENSATING);

        if (allSucceeded && !steps.isEmpty()) {
            this.sagaStatus = SagaStatus.COMPLETED;
        } else if (anyFailed || anyCompensating) {
            this.sagaStatus = SagaStatus.ABORTING;
        } else if (!steps.isEmpty() && steps.stream().anyMatch(s -> s.getStatus() == SagaStepStatus.STARTED)) {
            this.sagaStatus = SagaStatus.STARTED;
        } else {
            // 이상한 스텝이 있으면
        }
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

