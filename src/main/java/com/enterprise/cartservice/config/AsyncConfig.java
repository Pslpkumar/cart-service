package com.enterprise.cartservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
@Configuration
public class AsyncConfig {
    @Bean(name="cartTaskExecutor") public Executor cartTaskExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setCorePoolSize(4); e.setMaxPoolSize(10); e.setQueueCapacity(100);
        e.setThreadNamePrefix("CartAsync-"); e.initialize(); return e;
    }
}
