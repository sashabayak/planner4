// src/main/java/com/example/planner/mapper/ItemMapper.java
package com.example.planner.mapper;

import com.example.planner.dto.ItemDTO;
import com.example.planner.entity.Item;

public class ItemMapper {
  private ItemMapper() {}

  public static ItemDTO toDto(Item item) {
	if (item == null) return null;
	ItemDTO dto = new ItemDTO();
	dto.setId(item.getId());
	dto.setName(item.getName());
	dto.setDescription(item.getDescription());
	dto.setCompleted(item.isCompleted());
	dto.setCreatedAt(item.getCreatedAt());
	return dto;
  }

  public static Item toEntity(ItemDTO dto) {
	if (dto == null) return null;
	Item item = new Item();
	item.setId(dto.getId());
	item.setName(dto.getName());
	item.setDescription(dto.getDescription());
	item.setCompleted(dto.isCompleted());
	item.setCreatedAt(dto.getCreatedAt());
	return item;
  }
}