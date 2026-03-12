package com.example.planner.service;

import com.example.planner.dto.TagDTO;
import com.example.planner.entity.Item;
import com.example.planner.entity.Tag;
import com.example.planner.mapper.TagMapper;
import com.example.planner.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository repository;

  public List<TagDTO> findAll() {
	return repository.findAll().stream().map(TagMapper::toDto).toList();
  }

  public Optional<TagDTO> findById(Integer id) {
	return repository.findById(id).map(TagMapper::toDto);
  }

  public TagDTO save(TagDTO dto) {
	Tag tag = TagMapper.toEntity(dto);
	return TagMapper.toDto(repository.save(tag));
  }

  @Transactional
  public void deleteById(Integer id) {
	Tag tag = repository.findById(id)
		.orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
	for (Item item : tag.getItems()) {
	  item.getTags().remove(tag);
	}
	tag.getItems().clear();

	repository.save(tag);
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