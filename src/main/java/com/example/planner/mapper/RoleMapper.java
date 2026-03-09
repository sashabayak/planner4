package com.example.planner.mapper;

import com.example.planner.dto.RoleDTO;
import com.example.planner.entity.Role;

public class RoleMapper {
  private RoleMapper() {}

  public static RoleDTO toDto(Role role) {
	if (role == null) return null;
	RoleDTO dto = new RoleDTO();
	dto.setId(role.getId());
	dto.setName(role.getName());
	return dto;
  }

  public static Role toEntity(RoleDTO dto) {
	if (dto == null) return null;
	Role role = new Role();
	role.setId(dto.getId());
	role.setName(dto.getName());
	return role;
  }
}