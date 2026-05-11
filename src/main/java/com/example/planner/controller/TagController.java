package com.example.planner.controller;

import com.example.planner.dto.tag.TagDTO;
import com.example.planner.service.TagService;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
  private final TagService service;

  @GetMapping
  public List<TagDTO> getTags() {
	return service.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagDTO> getTagById(@PathVariable Integer id) {
	return service.findById(id)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public TagDTO addTag(@RequestBody TagDTO dto) {
	return service.save(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable Integer id) {
	service.deleteById(id);
	return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<TagDTO> updateTag(@PathVariable Integer id, @RequestBody TagDTO dto) {
	return service.update(id, dto)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
  }
}