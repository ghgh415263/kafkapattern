package com.example.kafkapattern.event;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    public static final String EVENT_ASYNC_TASK_EXECUTOR = "eventAsyncTaskExecutor";

    @Bean(EVENT_ASYNC_TASK_EXECUTOR)
    public ThreadPoolTaskExecutor eventAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);    // 최소 쓰레드 수
        executor.setMaxPoolSize(10);    // 최대 쓰레드 수
        executor.setQueueCapacity(100); // 작업 대기 큐 크기
        executor.setThreadNamePrefix("event-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);  // 우아한 종료 안하면 트랜잭션 끝나고 리스너가 호출되지 않을 수 있음. 트랜잭션 이후에 이 인스턴스가 종료되는 경우...
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }
}