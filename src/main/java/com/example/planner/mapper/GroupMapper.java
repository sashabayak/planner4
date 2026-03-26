package com.example.planner.mapper;

import com.example.planner.dto.group.GroupCreateDTO;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.entity.Group;

import org.springframework.stereotype.Component;
@Component
public class GroupMapper {

  public  GroupDTO toDto(Group group) {
	if (group == null) {
	  return null;
	}

	return GroupDTO.builder()
		.id(group.getId())
		.name(group.getName())
		.build();
  }

  public static Group toEntity(GroupCreateDTO createDTO) {
	if (createDTO == null) {
	  return null;
	}

	return Group.builder()
		.name(createDTO.getName())
		.build();
  }
}

