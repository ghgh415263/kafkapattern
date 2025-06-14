package com.example.kafkapattern.common.saga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {
    Optional<SagaStep> findBySagaInstanceAndStepName(SagaInstance sagaInstance, String stepName);
}
