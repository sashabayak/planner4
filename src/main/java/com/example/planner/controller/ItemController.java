package com.example.planner.controller;

import com.example.planner.dto.ErrorResponse;
import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.dto.item.ItemUpdateDTO;
import com.example.planner.service.ItemService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами")
public class ItemController {
  private final ItemService service;

  @GetMapping
  @Operation(summary = "Получить все задачи")
  public ResponseEntity<List<ItemDTO>> getAllItems() {
	return ResponseEntity.ok(service.getAllItems());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по ID")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Задача найден"),
	  @ApiResponse(responseCode = "404", description = "Задача не найден",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ItemDTO> getItemById(
	  @Parameter(description = "ID задачи", required = true) @PathVariable Long id) {
	ItemDTO item = service.getItemById(id);
	return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
  }

  @PostMapping
  @Operation(summary = "Создать новую задачу")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Задача создана"),
	  @ApiResponse(responseCode = "400", description = "Ошибка валидации",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemCreateDTO createDTO) {
	return ResponseEntity.ok(service.createItem(createDTO));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить задачу")
  public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id,
															@Valid @RequestBody ItemUpdateDTO updateDTO) {
	ItemDTO updated = service.updateItem(id, updateDTO);
	return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить задачу")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
	return service.deleteItem(id)
		? ResponseEntity.noContent().build()
		: ResponseEntity.notFound().build();
  }
}
