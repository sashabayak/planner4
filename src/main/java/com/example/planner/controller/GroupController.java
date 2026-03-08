// src/main/java/com/example/planner/controller/GroupController.java
package com.example.planner.controller;

import com.example.planner.dto.GroupDTO;
import com.example.planner.service.GroupService;
import lombok.RequiredArgsConstructor;
import com.example.planner.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.planner.dto.GroupWithUserRequest;
import java.util.List;

@RestController//аннотация Spring, которая помечает класс как контроллер,
               // обрабатывающий HTTP запросы и возвращающий данные в формате JSON/XML (не HTML страницы)
@RequestMapping("/api/groups")
@RequiredArgsConstructor // аннотация ломбок, которая автоматически генерирует конструктор для всех final полей и полей с аннотацией @NonNull
public class GroupController {
  private final GroupService service;// значение устанавливается один раз и не меняется

  // public GroupController(GroupService service) {
  //     this.service = service;
  // }

  @GetMapping
  public List<GroupDTO> getGroups() {
	return service.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<GroupDTO> getGroupById(@PathVariable Integer id) {
	return service.findById(id)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public GroupDTO addGroup(@RequestBody GroupDTO dto) {
	return service.save(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroup(@PathVariable Integer id) {
	service.deleteById(id);
	return ResponseEntity.noContent().build();
  }
  @PostMapping("/demonstrate/with-transaction")
  public ResponseEntity<String> createGroupWithUserWithTransaction(
	  @RequestBody GroupWithUserRequest request) {
	try {
	  service.createGroupWithUserWithTransaction(request.getGroup(), request.getUser());
	  return ResponseEntity.ok("Операция с транзакцией выполнена успешно");
	} catch (RuntimeException e) {
	  return ResponseEntity.badRequest().body(e.getMessage() + " - транзакция откачена, данные НЕ сохранены");
	}
  }

  @PostMapping("/demonstrate/without-transaction")
  public ResponseEntity<String> createGroupWithUserWithoutTransaction(
	  @RequestBody GroupWithUserRequest request) {
	try {
	  service.createGroupWithUserWithoutTransaction(request.getGroup(), request.getUser());
	  return ResponseEntity.ok("Операция без транзакции выполнена успешно");
	} catch (RuntimeException e) {
	  return ResponseEntity.badRequest().body(e.getMessage() + " - данные ЧАСТИЧНО сохранены в БД");
	}
  }

  @PutMapping("/{id}")
  public ResponseEntity<GroupDTO> updateGroup(@PathVariable Integer id, @RequestBody GroupDTO dto) {
	return service.update(id, dto)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }

}