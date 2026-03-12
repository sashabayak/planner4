package com.example.planner.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.planner.service.ItemService;
import com.example.planner.dto.ItemDTO;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
  private final ItemService service;

  @GetMapping
  public List<ItemDTO> getItems(@RequestParam(required = false) String name) {
	return service.searchByNameWithoutTags(name);  // ← без тегов
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
  @PostMapping("/{itemId}/tags/{tagId}")
  public ResponseEntity<String> addTagToItem(
	  @PathVariable Integer itemId,
	  @PathVariable Integer tagId) {
	try {
	  service.addTagToItem(itemId, tagId);
	  return ResponseEntity.ok("Тег добавлен задаче");
	} catch (Exception e) {
	  return ResponseEntity.badRequest().body(e.getMessage());
	}
  }
  @GetMapping("/with-tags")
  public List<ItemDTO> getItemsWithTags() {
	return service.findAllWithTags();
  }

  @GetMapping("/tag/{tagId}")
  public List<ItemDTO> getItemsByTag(@PathVariable Integer tagId) {
	return service.findItemsByTag(tagId);
  }
}