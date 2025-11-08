package com.example.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor(
            @Value("${app.async.mail.core-pool-size:2}") int corePoolSize,
            @Value("${app.async.mail.max-pool-size:4}") int maxPoolSize,
            @Value("${app.async.mail.queue-capacity:100}") int queueCapacity,
            @Value("${app.async.mail.thread-name-prefix:mail-}") String threadNamePrefix
    ) {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(corePoolSize);
        ex.setMaxPoolSize(maxPoolSize);
        ex.setQueueCapacity(queueCapacity);
        ex.setThreadNamePrefix(threadNamePrefix);
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(10);
        ex.initialize();
        return ex;
    }
}