package com.example.planner.mapper;

import com.example.planner.dto.item.ItemDTO;
import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

  public ItemDTO toDto(Item item) {
	if (item == null) {
	  return null;
	}

	return ItemDTO.builder()
		.id(item.getId())
		.name(item.getName())
		.description(item.getDescription())
		.completed(item.isCompleted())
		.createdAt(item.getCreatedAt())
		.build();
  }

  public Item toEntity(ItemCreateDTO item) {
	if (item == null) {
	  return null;
	}

	return Item.builder()
		.name(item.getName())
		.description(item.getDescription())
		.completed(item.getCompleted() != null ? item.getCompleted() : false)
		.build();
  }
}
