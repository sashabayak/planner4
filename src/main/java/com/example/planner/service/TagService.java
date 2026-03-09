package com.example.planner.service;

import com.example.planner.dto.TagDTO;
import com.example.planner.entity.Tag;
import com.example.planner.mapper.TagMapper;
import com.example.planner.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository repository;

  public List<TagDTO> findAll() {
	return repository.findAll().stream().map(TagMapper::toDto).collect(Collectors.toList());
  }

  public Optional<TagDTO> findById(Integer id) {
	return repository.findById(id).map(TagMapper::toDto);
  }

  public TagDTO save(TagDTO dto) {
	Tag tag = TagMapper.toEntity(dto);
	return TagMapper.toDto(repository.save(tag));
  }

  public void deleteById(Integer id) {
	repository.deleteById(id);
  }

  public Optional<TagDTO> update(Integer id, TagDTO dto) {
	return repository.findById(id)
		.map(tag -> {
		  tag.setName(dto.getName());
		  return TagMapper.toDto(repository.save(tag));
		});
  }
}