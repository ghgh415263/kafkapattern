package com.example.kafkapattern.common.saga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}
