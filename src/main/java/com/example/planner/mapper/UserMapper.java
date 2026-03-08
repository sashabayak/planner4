// src/main/java/com/example/planner/mapper/UserMapper.java
package com.example.planner.mapper;

import com.example.planner.dto.UserDTO;
import com.example.planner.entity.User;

public class UserMapper {
  private UserMapper() {}

  public static UserDTO toDto(User user) {
	if (user == null) return null;
	UserDTO dto = new UserDTO();
	dto.setId(user.getId());
	dto.setName(user.getName());
	dto.setBirthDate(user.getBirthDate());
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