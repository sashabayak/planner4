package com.example.planner.mapper;

import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.entity.Item;

import org.springframework.stereotype.Component;
@Component
public class ItemMapper {

  private final UserMapper userMapper;

  public ItemMapper(UserMapper userMapper) {
	this.userMapper = userMapper;
  }

  public ItemDTO toDto(Item item) {
	if (item == null) return null;

	ItemDTO.ItemDTOBuilder builder = ItemDTO.builder()
		.id(item.getId())
		.name(item.getName())
		.description(item.getDescription())
		.completed(item.isCompleted())
		.createdAt(item.getCreatedAt());

	if (item.getTags() != null && !item.getTags().isEmpty()) {
	  builder.tags(item.getTags().stream()
		  .map(TagMapper::toDto)
		  .toList());
	}

	if (item.getUsers() != null && !item.getUsers().isEmpty()) {
	  builder.users(item.getUsers().stream()
		  .map(userMapper::toDto)
		  .toList());
	}

	return builder.build();
  }

  public Item toEntity(ItemCreateDTO item) {
	if (item == null) {
	  return null;
	}

	return Item.builder()
		.name(item.getName())
		.description(item.getDescription())
		.completed(Boolean.TRUE.equals(item.getCompleted()))
		.build();
  }
}
