package com.example.planner.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.planner.dto.ErrorResponse;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.group.GroupCreateDTO;
import com.example.planner.dto.group.GroupUpdateDTO;

import com.example.planner.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Группы", description = "Управление группами планировщика")
public class GroupController {
  private final GroupService service;

  @GetMapping
  @Operation(summary = "Получить все группы", description = "Возвращает список всех групп")
  @ApiResponse(responseCode = "200", description = "Успешное получение списка")
  public ResponseEntity<List<GroupDTO>> getAllGroups() {
	return ResponseEntity.ok(service.getAllGroups());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить  по ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Группа найдена"),
      @ApiResponse(responseCode = "404", description = "Группа не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<GroupDTO> getGroupById(
      @Parameter(description = "ID группы", required = true) @PathVariable Long id) {
    GroupDTO client = service.getGroupById(id);
    return client == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(client);
  }

  @PostMapping
  @Operation(summary = "Создать новую группу")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Группа создана"),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody GroupCreateDTO createDto) {
    return ResponseEntity.ok(service.createGroup(createDto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить группу")
  public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id,
                                                @Valid @RequestBody GroupUpdateDTO updateDTO) {
    GroupDTO updated = service.updateGroup(id, updateDTO);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить группу")
  public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
    return service.deleteGroup(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

}

//  @GetMapping("/{id}")
//  public ResponseEntity<GroupDTO> getGroupById(@PathVariable Integer id) {
//	return service.findById(id)
//		.map(ResponseEntity::ok)
//		.orElseGet(() -> ResponseEntity.notFound().build());
//  }
//
//  @PostMapping
//  public GroupDTO addGroup(@RequestBody GroupDTO dto) {
//	return service.save(dto);
//  }
//
//  @DeleteMapping("/{id}")
//  public ResponseEntity<Void> deleteGroup(@PathVariable Integer id) {
//	service.deleteById(id);
//	return ResponseEntity.noContent().build();
//  }
//
//  @PostMapping("/demonstrate/with-transaction")
//  public ResponseEntity<String> createGroupWithUserWithTransaction(
//	  @RequestBody GroupWithUserRequest request) {
//	try {
//	  service.createGroupWithUserWithTransaction(request.getGroup(), request.getUser());
//	  return ResponseEntity.ok("Операция с транзакцией выполнена успешно");
//	} catch (RuntimeException e) {
//	  return ResponseEntity.badRequest().body(e.getMessage() + " - транзакция откачена, данные НЕ сохранены");
//	}
//  }
//
//  @PostMapping("/demonstrate/without-transaction")
//  public ResponseEntity<String> createGroupWithUserWithoutTransaction(
//	  @RequestBody GroupWithUserRequest request) {
//	try {
//	  service.createGroupWithUserWithoutTransaction(request.getGroup(), request.getUser());
//	  return ResponseEntity.ok("Операция без транзакции выполнена успешно");
//	} catch (RuntimeException e) {
//	  return ResponseEntity.badRequest().body(e.getMessage() + " - данные ЧАСТИЧНО сохранены в БД");
//	}
//  }
//
//  @PutMapping("/{id}")
//  public ResponseEntity<GroupDTO> updateGroup(@PathVariable Integer id, @RequestBody GroupDTO dto) {
//	return service.update(id, dto)
//		.map(ResponseEntity::ok)
//		.orElseGet(() -> ResponseEntity.notFound().build());
//  }
//  @GetMapping("/with-users")
//  public List<GroupDTO> getGroupsWithUsers() {
//	return service.findAllWithUsers();
//  }
//
//  @GetMapping("/{id}/with-users")
//  public ResponseEntity<GroupDTO> getGroupWithUsersById(@PathVariable Integer id) {
//	return service.findByIdWithUsers(id)
//		.map(ResponseEntity::ok)
//		.orElseGet(() -> ResponseEntity.notFound().build());
//  }
//}