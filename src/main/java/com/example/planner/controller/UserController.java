package com.example.planner.controller;

import com.example.planner.dto.ErrorResponse;
import com.example.planner.dto.user.UserCreateDTO;
import com.example.planner.dto.user.UserDTO;
import com.example.planner.dto.user.UserFilterDTO;
import com.example.planner.dto.user.UserUpdateDTO;
import com.example.planner.service.UserService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление пользователями")
@Validated
public class UserController {
  private final UserService userService;


  @GetMapping("/search-jpql")
  @Operation(summary = "Поиск пользователей через JPQL")
  public ResponseEntity<List<UserDTO>> searchSessionsJpql(
	  @RequestParam(required = false) String groupName,
	  @RequestParam(required = false) String roleName) {

	UserFilterDTO filter = UserFilterDTO.builder()
		.groupName(groupName)
		.roleName(roleName)
		.build();

	return ResponseEntity.ok(userService.getUserWithFiltersJpql(filter));
  }

  @GetMapping("/search-native")
  @Operation(summary = "Поиск пользователей через Native Query")
  public ResponseEntity<List<UserDTO>> searchSessionsNative(
	  @RequestParam(required = false) String groupName,
	  @RequestParam(required = false) String roleName) {

	UserFilterDTO filter = UserFilterDTO.builder()
		.roleName(groupName)
		.groupName(roleName)
		.build();

	return ResponseEntity.ok(userService.getUserWithFiltersNative(filter));
  }

  @GetMapping("/search-cached")
  @Operation(summary = "Поиск пользователей с использованием кэша")
  public ResponseEntity<Page<UserDTO>> searchSessionsCached(
	  @RequestParam(required = false) String groupName,
	  @RequestParam(required = false) String roleName,
	  @RequestParam(defaultValue = "0") int page,
	  @RequestParam(defaultValue = "10") int size) {

	UserFilterDTO filter = UserFilterDTO.builder()
		.groupName(groupName)
		.roleName(roleName)
		.page(page)
		.size(size)
		.build();

	return ResponseEntity.ok(userService.getUsersWithCache(filter));
  }

  @GetMapping("/search-paginated")
  @Operation(summary = "Поиск пользователей с пагинацией")
  public ResponseEntity<Page<UserDTO>> searchSessionsPaginated(
	  @RequestParam(required = false) String groupName,
	  @RequestParam(required = false) String roleName,
	  @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") @Min(0 )int page,
	  @RequestParam(defaultValue = "10") @Parameter(description = "Размер страницы") @Min(1)int size,
	  @RequestParam(defaultValue = "name") String sortBy,
	  @RequestParam(defaultValue = "ASC") String sortDirection) {

	UserFilterDTO filter = UserFilterDTO.builder()
		.groupName(groupName)
		.roleName(roleName)
		.page(page)
		.size(size)
		.sortBy(sortBy)
		.sortDirection(sortDirection)
		.build();

	return ResponseEntity.ok(userService.getUsersWithFiltersPaged(filter));
  }



  @GetMapping("/cache/stats")
  @Operation(summary = "Получить статистику кэша")
  public ResponseEntity<String> getCacheStats() {
	return ResponseEntity.ok("Текущий размер кэша: " + userService.getCacheSize());
  }

  @GetMapping("/group/{groupId}")
  @Operation(summary = "Получить пользователей по ID группы")
  public ResponseEntity<List<UserDTO>> getUsersByGroupId(
	  @Parameter(description = "ID пользователя", required = true) @PathVariable @Min(1) Long groupId) {
	return ResponseEntity.ok(userService.getUsersByGroupId(groupId));
  }
  @GetMapping("/{id}")
  @Operation(summary = "Получить пользователя по ID")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Пользователь найдена"),
	  @ApiResponse(responseCode = "404", description = "Пользователь не найдена",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UserDTO> getUserById(
	  @Parameter(description = "ID пользователя", required = true) @PathVariable @Min(1) Long id) {
    	return ResponseEntity.ok(userService.getUserById(id));
  }
  @PostMapping
  @Operation(summary = "Добавить пользователя")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Пользователь добавлен"),
	  @ApiResponse(responseCode = "400", description = "Ошибка валидации",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
	return ResponseEntity.ok(userService.createUser(createDTO));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить пользователя")
  public ResponseEntity<UserDTO> updateUser(
	  @PathVariable @Min(1) Long id, @Valid @RequestBody UserUpdateDTO updateDto) {
	UserDTO updated = userService.updateUser(id, updateDto);
	return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить пользователя")
  public ResponseEntity<Void> deleteUser(@Min(1) @PathVariable Long id) {
	return userService.deleteUser(id)
		? ResponseEntity.noContent().build()
		: ResponseEntity.notFound().build();
  }

  @GetMapping("/demonstrate/nplus1")
  @Operation(summary = "Демонстрация N+1 проблемы")
  public ResponseEntity<String> demonstrateNPlus1() {
	userService.demonstrateNPlus1Problem();
	return ResponseEntity.ok("N+1 проблема продемонстрирована. Проверьте логи.");
  }

  @GetMapping("/demonstrate/entity-graph")
  @Operation(summary = "Демонстрация решения с EntityGraph")
  public ResponseEntity<String> demonstrateEntityGraph() {
	userService.demonstrateEntityGraphSolution();
	return ResponseEntity.ok("Решение с EntityGraph продемонстрировано. Проверьте логи.");
  }

  @PostMapping("/demonstrate/with-transaction")
  @Operation(summary = "Демонстрация создания с транзакцией")
  public ResponseEntity<String> createWithTransaction(@RequestBody UserCreateDTO createDto) {
	try {
	  userService.createWithRelatedWithTransaction(createDto);
	  return ResponseEntity.ok("Операция с транзакцией выполнена успешно");
	} catch (RuntimeException e) {
	  return ResponseEntity.badRequest().body(e.getMessage() + " - транзакция откачена");
	}
  }

  @PostMapping("/demonstrate/without-transaction")
  @Operation(summary = "Демонстрация создания без транзакции")
  public ResponseEntity<String> createWithoutTransaction(@RequestBody UserCreateDTO createDto) {
	try {
	  userService.createWithRelatedWithoutTransaction(createDto);
	  return ResponseEntity.ok("Операция без транзакции выполнена успешно");
	} catch (RuntimeException e) {
	  return ResponseEntity.badRequest().body(e.getMessage() + " - данные сохранены в БД");
	}
  }
  @GetMapping
  @Operation(summary = "Получить всех пользователей")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
	return ResponseEntity.ok(userService.getAllUsers());
  }
}