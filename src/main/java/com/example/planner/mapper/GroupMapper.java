package com.example.planner.mapper;

import com.example.planner.entity.Group;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.group.GroupCreateDTO;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

  public GroupDTO toDto(Group group) {
	if (group == null) {
	  return null;
	}

	return GroupDTO.builder()
		.id(group.getId())
		.name(group.getName())
		.build();
  }

//	GroupDTO.GroupDTOBuilder builder = GroupDTO.builder()
//	GroupDTO dto = new GroupDTO();
//	dto.setId(group.getId());
//	dto.setName(group.getName());
//	if (group.getUsers() != null) {
//	  dto.setUsers(group.getUsers().stream()
//		  .map(UserMapper::toDto)
//		  .toList());
//	}
//	return dto;


  public static Group toEntity(GroupCreateDTO createDTO) {
	if (createDTO == null) {
	  return null;
	}

	return Group.builder()
		.name(createDTO.getName())
		.build();
//	Group group = new Group();
//	group.setId(dto.getId());
//	group.setName(dto.getName());
//	return group;
  }
}

