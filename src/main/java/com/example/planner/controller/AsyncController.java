package com.example.planner.controller;

import com.example.planner.dto.async.AsyncTaskResponse;
import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.service.async.AsyncItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/async/items")
@RequiredArgsConstructor
@Tag(name = "Асинхронные операции", description = "Асинхронное создание задач (Item)")
public class AsyncController {

  private final AsyncItemService asyncItemService;

  @PostMapping
  @Operation(summary = "Асинхронное создание задачи")
  public ResponseEntity<AsyncTaskResponse> createItemAsync(@RequestBody ItemCreateDTO itemDto) {
	String taskId = asyncItemService.createItemAsync(itemDto);

	AsyncTaskResponse response = AsyncTaskResponse.builder()
		.taskId(taskId)
		.status("ACCEPTED")
		.createdAt(LocalDateTime.now())
		.message("Задача на создание айтема принята")
		.build();

	return ResponseEntity.accepted().body(response);
  }

  @GetMapping("/tasks/{taskId}")
  @Operation(summary = "Получить статус асинхронной задачи")
  public ResponseEntity<AsyncTaskResponse> getTaskStatus(@PathVariable String taskId) {
	AsyncTaskResponse response = asyncItemService.getTaskStatus(taskId);
	if (response == null) {
	  return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(response);
  }

  @GetMapping("/tasks")
  @Operation(summary = "Получить все асинхронные задачи")
  public ResponseEntity<Map<String, AsyncTaskResponse>> getAllTasks() {
	return ResponseEntity.ok(asyncItemService.getAllTasks());
  }
}