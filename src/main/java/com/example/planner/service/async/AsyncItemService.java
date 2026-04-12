package com.example.planner.service.async;

import com.example.planner.dto.async.AsyncTaskResponse;
import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncItemService {

  private final ItemService itemService;
  private final ApplicationContext applicationContext;
  private final Map<String, AsyncTaskResponse> taskStore = new ConcurrentHashMap<>();

  public String createItemAsync(ItemCreateDTO request) {
	String taskId = UUID.randomUUID().toString();

	AsyncTaskResponse response = AsyncTaskResponse.builder()
		.taskId(taskId)
		.status("IN_PROGRESS")
		.createdAt(LocalDateTime.now())
		.message("Создание задачи начато...")
		.build();
	taskStore.put(taskId, response);

	AsyncItemService proxy = applicationContext.getBean(AsyncItemService.class);
	proxy.executeAsync(taskId, request);

	return taskId;
  }

  @Async("taskExecutor")
  @Transactional
  public void executeAsync(String taskId, ItemCreateDTO request) {
	log.info("Асинхронная задача {} начата для айтема: {}", taskId, request.getName());

	try {
	  log.info("Задержка 15 секунд перед созданием айтема...");
	  Thread.sleep(15000);

	  ItemDTO result = itemService.createItem(request);

	  AsyncTaskResponse response = taskStore.get(taskId);
	  response.setStatus("COMPLETED");
	  response.setCompletedAt(LocalDateTime.now());
	  response.setResult(result);
	  response.setResourceId(result.getId());
	  response.setResourceName(result.getName());
	  response.setMessage("Задача успешно создана с ID: " + result.getId());

	  log.info("Асинхронная задача {} завершена успешно. ID созданной задачи: {}", taskId, result.getId());

	} catch (InterruptedException e) {
	  log.error("Асинхронная задача {} была прервана", taskId);
	  Thread.currentThread().interrupt();
	  AsyncTaskResponse response = taskStore.get(taskId);
	  response.setStatus("FAILED");
	  response.setCompletedAt(LocalDateTime.now());
	  response.setError("Задача была прервана");
	  response.setMessage("Операция прервана");
	} catch (Exception e) {
	  log.error("Ошибка в асинхронной задаче {}: {}", taskId, e.getMessage());
	  AsyncTaskResponse response = taskStore.get(taskId);
	  response.setStatus("FAILED");
	  response.setCompletedAt(LocalDateTime.now());
	  response.setError(e.getMessage());
	  response.setMessage("Ошибка при создании задачи: " + e.getMessage());
	}
  }

  public AsyncTaskResponse getTaskStatus(String taskId) {
	return taskStore.get(taskId);
  }

  public Map<String, AsyncTaskResponse> getAllTasks() {
	return taskStore;
  }
}