package com.example.kafkapattern.common.saga;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_instance_id", nullable = false)
    private SagaInstance sagaInstance;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SagaStepStatus status;

    public SagaStep(SagaInstance sagaInstance, String stepName) {
        this.sagaInstance = sagaInstance;
        this.stepName = stepName;
        this.status = SagaStepStatus.STARTED;
    }

    public void setStatus(SagaStepStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SagaStep sagaStep = (SagaStep) o;
        return Objects.equals(id, sagaStep.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SagaStep{" +
                "id=" + id +
                ", stepName='" + stepName + '\'' +
                ", status=" + status +
                '}';
    }

    public void setSagaInstance(SagaInstance sagaInstance) {
        this.sagaInstance = sagaInstance;
    }
}

