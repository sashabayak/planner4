package com.example.planner.controller;

import com.example.planner.dto.role.RoleDTO;
import com.example.planner.dto.role.RoleCreateDTO;
import com.example.planner.dto.role.RoleUpdateDTO;
import com.example.planner.dto.ErrorResponse;
import com.example.planner.service.RoleService;
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

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Роли", description = "Управление ролями")
public class RoleController {
  private final RoleService service;

  @GetMapping
  @Operation(summary = "Получить все роли")
  public ResponseEntity<List<RoleDTO>> getAllRoles() {
	return ResponseEntity.ok(service.getAllRoles());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить роль по ID")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Роль найдена"),
	  @ApiResponse(responseCode = "404", description = "Роль не найдена",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<RoleDTO> getRoleById(
	  @Parameter(description = "ID роли", required = true) @PathVariable Long id) {
	RoleDTO Role = service.getRoleById(id);
	return Role == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(Role);
  }

  @PostMapping
  @Operation(summary = "Создать новую роль")
  @ApiResponses(value = {
	  @ApiResponse(responseCode = "200", description = "Роль создана"),
	  @ApiResponse(responseCode = "400", description = "Ошибка валидации",
		  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleCreateDTO createDto) {
	return ResponseEntity.ok(service.createRole(createDto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить роль")
  public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id,
															@Valid @RequestBody RoleUpdateDTO updateDTO) {
	RoleDTO updated = service.updateRole(id, updateDTO);
	return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить роль")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
	return service.deleteRole(id)
		? ResponseEntity.noContent().build()
		: ResponseEntity.notFound().build();
  }
}
//package com.example.planner.controller;
//
//import com.example.planner.service.RoleService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import com.example.planner.dto.role.RoleDTO;
//
//@RestController
//@RequestMapping("/api/roles")
//@RequiredArgsConstructor
//public class RoleController {
//  private final RoleService service;
//
//  @GetMapping
//  public List<RoleDTO> getRoles() {
//	return service.findAll();
//  }
//
//  @GetMapping("/{id}")
//  public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer id) {
//	return service.findById(id)
//		.map(ResponseEntity::ok)
//		.orElseGet(() -> ResponseEntity.notFound().build());
//  }
//
//  @PostMapping
//  public RoleDTO addRole(@RequestBody RoleDTO dto) {
//	return service.save(dto);
//  }
//
//  @DeleteMapping("/{id}")
//  public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
//	service.deleteById(id);
//	return ResponseEntity.noContent().build();
//  }
//
//  @PutMapping("/{id}")
//  public ResponseEntity<RoleDTO> updateRole(@PathVariable Integer id, @RequestBody RoleDTO dto) {
//	return service.update(id, dto)
//		.map(ResponseEntity::ok)
//		.orElseGet(() -> ResponseEntity.notFound().build());
//  }
//}