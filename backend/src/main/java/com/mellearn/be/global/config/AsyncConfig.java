package com.mellearn.be.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
@Slf4j
public class AsyncConfig {

    // CPU 코어 수에 따른 동적 설정
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_CORES * 2;
    private static final int MAX_POOL_SIZE = CPU_CORES * 4;
    private static final int QUEUE_CAPACITY = 500;
    private static final String THREAD_NAME_PREFIX = "async-quiz-";
    private static final boolean WAIT_TASK_COMPLETE = true;
    private static final long KEEP_ALIVE_TIME = 60L;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(WAIT_TASK_COMPLETE);
        executor.setKeepAliveSeconds((int) KEEP_ALIVE_TIME);
        executor.initialize();
        return executor;
    }
}
