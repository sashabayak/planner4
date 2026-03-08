// src/main/java/com/example/planner/mapper/GroupMapper.java
package com.example.planner.mapper;

import com.example.planner.dto.GroupDTO;
import com.example.planner.entity.Group;

public class GroupMapper {
  private GroupMapper() {}

  public static GroupDTO toDto(Group group) {
	if (group == null) return null;
	GroupDTO dto = new GroupDTO();
	dto.setId(group.getId());
	dto.setName(group.getName());
	return dto;
  }

  public static Group toEntity(GroupDTO dto) {
	if (dto == null) return null;
	Group group = new Group();
	group.setId(dto.getId());
	group.setName(dto.getName());
	return group;
  }
}