package com.example.planner.mapper;

import com.example.planner.dto.user.UserCreateDTO;
import com.example.planner.dto.user.UserDTO;
import com.example.planner.entity.Group;
import com.example.planner.entity.Role;
import com.example.planner.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDTO toDto(User user) {
	if (user == null) {
	  return null;
	}

	UserDTO.UserDTOBuilder builder = UserDTO.builder()
		.id(user.getId())
		.name(user.getName())
		.birthDate(user.getBirthDate());

	if (user.getGroup() != null) {
	  builder.groupId(user.getGroup().getId())
		  .groupName(user.getGroup().getName());
	}

	if (user.getRole() != null) {
	  builder.roleId(user.getRole().getId())
		  .roleName(user.getRole().getName());
	}

	builder.itemsCount(user.getItems() != null ? user.getItems().size() : 0);

	return builder.build();
  }

  public User toEntity(UserCreateDTO createDTO, Role role, Group group) {
	if (createDTO == null) {
	  return null;
	}

	User.UserBuilder builder = User.builder()
		.name(createDTO.getName())
		.birthDate(createDTO.getBirthDate())
		.role(role)
		.group(group);

	return builder.build();
  }
}