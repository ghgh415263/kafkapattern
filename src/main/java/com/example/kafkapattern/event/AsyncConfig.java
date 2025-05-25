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
        executor.initialize();
        return executor;
    }
}