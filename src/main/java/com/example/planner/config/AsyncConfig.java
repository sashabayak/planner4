package com.example.planner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

	executor.setCorePoolSize(200);
	executor.setMaxPoolSize(1000);
	executor.setQueueCapacity(10000);
	executor.setThreadNamePrefix("async-");
	executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	executor.setWaitForTasksToCompleteOnShutdown(true);
	executor.setAwaitTerminationSeconds(120);
	executor.initialize();

	return executor;
  }
}