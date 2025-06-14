package com.example.kafkapattern.common.saga;

public enum StepResult {
    COMPLETED_AND_CONTINUE,  // 이 단계 즉시 완료 & 다음 단계 바로 실행 가능
    COMPLETED_AND_WAIT
}
