package com.example.planner.controller;

import com.example.planner.dto.UserDTO;
import com.example.planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService service;

  @GetMapping
  public List<UserDTO> getUsers() {
	return service.findAll();
  }

  @GetMapping("/with-nplusone")
  public List<UserDTO> getUsersWithNPlusOne() {
	return service.findAllWithNPlusOne();
  }

  @GetMapping("/without-nplusone")
  public List<UserDTO> getUsersWithoutNPlusOne() {
	return service.findAllWithoutNPlusOne();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
	return service.findById(id)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public UserDTO addUser(@RequestBody UserDTO dto) {
	return service.save(dto);
  }

  @PostMapping("/{userId}/items/{itemId}")
  public ResponseEntity<String> addItemToUser(
	  @PathVariable Integer userId,
	  @PathVariable Integer itemId) {
	try {
	  service.addItemToUser(userId, itemId);
	  return ResponseEntity.ok("Задача добавлена пользователю");
	} catch (Exception e) {
	  return ResponseEntity.badRequest().body(e.getMessage());
	}
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
	service.deleteById(id);
	return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
	return service.update(id, dto)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }
}