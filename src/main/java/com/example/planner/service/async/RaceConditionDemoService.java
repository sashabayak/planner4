package com.example.planner.service.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceConditionDemoService {

  private final CounterService counterService;

  public String demonstrateRaceCondition() throws InterruptedException {
	return executeTest(false);
  }

  public String demonstrateSolution() throws InterruptedException {
	return executeTest(true);
  }

  private String executeTest(boolean useSynchronized) throws InterruptedException {
	counterService.resetCounters();

	int threadCount = 100;
	int incrementsPerThread = 1000;
	int expectedTotal = threadCount * incrementsPerThread;

	String testType = useSynchronized ? "РЕШЕНИЕ ЧЕРЕЗ SYNCHRONIZED" : "ДЕМОНСТРАЦИЯ RACE CONDITION";
	log.info("=== {} ===", testType);

	long startTime = System.currentTimeMillis();

	ExecutorService executor = Executors.newFixedThreadPool(threadCount);

	try {
	  for (int i = 0; i < threadCount; i++) {
		executor.submit(() -> {
		  for (int j = 0; j < incrementsPerThread; j++) {
			if (useSynchronized) {
			  counterService.incrementSync();
			} else {
			  counterService.incrementUnsafe();
			}
		  }
		});
	  }

	  executor.shutdown();

	  boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
	  long endTime = System.currentTimeMillis();

	  if (!finished) {
		executor.shutdownNow();
		return "ОШИБКА: Таймаут выполнения";
	  }

	  long result = useSynchronized ? counterService.getSyncCounter() : counterService.getUnsafeCounter();
	  long lost = expectedTotal - result;

	  log.info("{} результат: {} (ожидалось: {})", testType, result, expectedTotal);
	  log.info("Время выполнения: {} ms", endTime - startTime);

	  return String.format("%s: результат=%d (ожидалось=%d, потеряно=%d, время=%d ms)",
		  useSynchronized ? "Synchronized решение" : "Race condition проблема",
		  result, expectedTotal, lost, endTime - startTime);

	} catch (Exception e) {
	  log.error("Ошибка при выполнении теста: {}", e.getMessage());
	  return "ОШИБКА: " + e.getMessage();
	} finally {
	  if (!executor.isTerminated()) {
		executor.shutdownNow();
	  }
	}
  }
}