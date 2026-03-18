package com.example.planner.mapper;

import com.example.planner.dto.role.RoleDTO;
import com.example.planner.dto.role.RoleCreateDTO;
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
//package com.example.planner.mapper;
//
//import com.example.planner.entity.Role;
//import com.example.planner.dto.role.RoleDTO;
//
//public class RoleMapper {
//  private RoleMapper() {}
//
//  public static RoleDTO toDto(Role role) {
//	if (role == null) return null;
//	RoleDTO dto = new RoleDTO();
//	dto.setId(role.getId());
//	dto.setName(role.getName());
//	return dto;
//  }
//
//  public static Role toEntity(RoleDTO dto) {
//	if (dto == null) return null;
//	Role role = new Role();
//	role.setId(dto.getId());
//	role.setName(dto.getName());
//	return role;
//  }
//}