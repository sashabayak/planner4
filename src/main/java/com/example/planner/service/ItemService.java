package com.example.planner.service;

import com.example.planner.dto.ItemDTO;
import com.example.planner.entity.Item;
import com.example.planner.mapper.ItemMapper;
import com.example.planner.repository.ItemRepository;
import com.example.planner.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import com.example.planner.entity.User;
import com.example.planner.entity.Tag;


@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository repository;
  private final TagRepository tagRepository;

  public List<ItemDTO> findAll() {
	return repository.findAll().stream().map(ItemMapper::toDto).toList();
  }

  public Optional<ItemDTO> findById(Integer id) {
	return repository.findById(id).map(ItemMapper::toDto);
  }

  public ItemDTO save(ItemDTO dto) {
	Item item = ItemMapper.toEntity(dto);
	return ItemMapper.toDto(repository.save(item));
  }


  @Transactional
  public void deleteById(Integer id) {
	Item item = repository.findById(id)
		.orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
	for (User user : item.getUsers()) {
	  user.getItems().remove(item);
	}
	item.getUsers().clear();
	for (Tag tag : item.getTags()) {
	  tag.getItems().remove(item);
	}
	item.getTags().clear();
	repository.save(item);
	repository.deleteById(id);
  }

  public List<ItemDTO> searchByName(String name) {
	String keyword = (name == null || name.trim().isEmpty()) ? "" : name;
	return repository.findByNameContainingIgnoreCase(keyword)
		.stream().map(ItemMapper::toDto).toList();
  }

  public Optional<ItemDTO> update(Integer id, ItemDTO dto) {
	return repository.findById(id)
		.map(item -> {
		  item.setName(dto.getName());
		  item.setDescription(dto.getDescription());
		  item.setCompleted(dto.isCompleted());
		  return ItemMapper.toDto(repository.save(item));
		});
  }
  @Transactional
  public void addTagToItem(Integer itemId, Integer tagId) {
	Item item = repository.findById(itemId)
		.orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

	Tag tag = tagRepository.findById(tagId)
		.orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

	item.getTags().add(tag);
	repository.save(item);
  }
  public List<ItemDTO> findAllWithTags() {
	return repository.findAllWithTags().stream()
		.map(ItemMapper::toDto)
		.toList();
  }

  public List<ItemDTO> findItemsByTag(Integer tagId) {
	return repository.findItemsByTagId(tagId).stream()
		.map(ItemMapper::toDto)
		.toList();
  }
  public List<ItemDTO> searchByNameWithoutTags(String name) {
	String keyword = (name == null || name.trim().isEmpty()) ? "" : name;
	return repository.findByNameContainingIgnoreCase(keyword)
		.stream()
		.map(item -> {
		  ItemDTO dto = ItemMapper.toDto(item);
		  dto.setTags(null);  // ← убираем теги
		  return dto;
		})
		.toList();
  }
}