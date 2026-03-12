package com.example.planner.mapper;

import com.example.planner.dto.UserDTO;
import com.example.planner.entity.User;
import java.util.stream.Collectors;

public class UserMapper {
  private UserMapper() {}

  public static UserDTO toDto(User user) {
	if (user == null) return null;
	UserDTO dto = new UserDTO();
	dto.setId(user.getId());
	dto.setName(user.getName());
	dto.setBirthDate(user.getBirthDate());
	if (user.getGroup() != null) {
	  dto.setGroupId(user.getGroup().getId());
	}
	if (user.getItems() != null) {
	  dto.setItems(user.getItems().stream()
		  .map(ItemMapper::toDto)
		  .collect(Collectors.toList()));
	}
	if (user.getRole() != null) {
	  dto.setRoleId(user.getRole().getId());
	}
	return dto;
  }
  public static UserDTO toDtoWithoutExtra(User user) {
	if (user == null) return null;
	UserDTO dto = new UserDTO();
	dto.setId(user.getId());
	dto.setName(user.getName());
	dto.setBirthDate(user.getBirthDate());

	if (user.getGroup() != null) {
	  dto.setGroupId(user.getGroup().getId());
	}
	if (user.getRole() != null) {
	  dto.setRoleId(user.getRole().getId());
	}

	if (user.getItems() != null) {
	  dto.setItems(user.getItems().stream()
		  .map(ItemMapper::toDtoWithoutTags)  // ← используем метод без тегов
		  .toList());
	}

	return dto;
  }
  public static User toEntity(UserDTO dto) {
	if (dto == null) return null;
	User user = new User();
	user.setId(dto.getId());
	user.setName(dto.getName());
	user.setBirthDate(dto.getBirthDate());
	return user;
  }
}