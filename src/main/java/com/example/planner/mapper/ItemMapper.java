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
//package com.example.planner.mapper;
//
//import com.example.planner.entity.Item;
//import com.example.planner.dto.item.ItemDTO;
//
//public class ItemMapper {
//  private ItemMapper() {}
//
//  public static ItemDTO toDto(Item item) {
//	if (item == null) return null;
//	ItemDTO dto = new ItemDTO();
//	dto.setId(item.getId());
//	dto.setName(item.getName());
//	dto.setDescription(item.getDescription());
//	dto.setCompleted(item.isCompleted());
//	dto.setCreatedAt(item.getCreatedAt());
//	if (item.getTags() != null) {
//	  dto.setTags(item.getTags().stream()
//		  .map(TagMapper::toDto)
//		  .toList());
//	}
//	return dto;
//  }
//  public static ItemDTO toDtoWithoutTags(Item item) {
//	if (item == null) return null;
//	ItemDTO dto = new ItemDTO();
//	dto.setId(item.getId());
//	dto.setName(item.getName());
//	dto.setDescription(item.getDescription());
//	dto.setCompleted(item.isCompleted());
//	dto.setCreatedAt(item.getCreatedAt());
//	// НЕ копируем tags!
//	return dto;
//  }
//  public static Item toEntity(ItemDTO dto) {
//	if (dto == null) return null;
//	Item item = new Item();
//	item.setId(dto.getId());
//	item.setName(dto.getName());
//	item.setDescription(dto.getDescription());
//	item.setCompleted(dto.isCompleted());
//	item.setCreatedAt(dto.getCreatedAt());
//	return item;
//  }
//}