// src/main/java/com/example/planner/controller/ItemController.java
package com.example.planner.controller;

import com.example.planner.dto.ItemDTO;
import com.example.planner.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
  private final ItemService service;

  @GetMapping
  public List<ItemDTO> getItems(@RequestParam(required = false) String name) {
	return service.searchByName(name);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ItemDTO> getItemById(@PathVariable Integer id) {
	return service.findById(id)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ItemDTO addItem(@RequestBody ItemDTO dto) {
	return service.save(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
	service.deleteById(id);
	return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<ItemDTO> updateItem(@PathVariable Integer id, @RequestBody ItemDTO dto) {
	return service.update(id, dto)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }
}