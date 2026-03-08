// src/main/java/com/example/planner/mapper/TagMapper.java
package com.example.planner.mapper;

import com.example.planner.dto.TagDTO;
import com.example.planner.entity.Tag;

public class TagMapper {
  private TagMapper() {}

  public static TagDTO toDto(Tag tag) {
	if (tag == null) return null;
	TagDTO dto = new TagDTO();
	dto.setId(tag.getId());
	dto.setName(tag.getName());
	return dto;
  }

  public static Tag toEntity(TagDTO dto) {
	if (dto == null) return null;
	Tag tag = new Tag();
	tag.setId(dto.getId());
	tag.setName(dto.getName());
	return tag;
  }
}