package com.example.planner.mapper;

import com.example.planner.dto.role.RoleCreateDTO;
import com.example.planner.dto.role.RoleDTO;
import com.example.planner.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

  public RoleDTO toDto(Role role) {
	if (role == null) {
	  return null;
	}

	return RoleDTO.builder()
		.id(role.getId())
		.name(role.getName())
		.build();
  }

  public Role toEntity(RoleCreateDTO role) {
	if (role == null) {
	  return null;
	}

	return Role.builder()
		.name(role.getName())
		.build();
  }
}
