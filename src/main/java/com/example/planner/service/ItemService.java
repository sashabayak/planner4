package com.example.planner.service;


import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.dto.item.ItemUpdateDTO;
import com.example.planner.entity.Item;
import com.example.planner.entity.Tag;
import com.example.planner.mapper.ItemMapper;
import com.example.planner.repository.ItemRepository;

import java.util.List;
import java.util.NoSuchElementException;

import com.example.planner.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository repository;
  private final ItemMapper mapper;
  private final TagRepository tagRepository;

  public List<ItemDTO> getAllItems() {
	return repository.findAll()
		.stream()
		.map(mapper::toDto)
		.toList();
  }

  public ItemDTO getItemById(Long id) {
	return repository.findById(id)
		.map(mapper::toDto)
		.orElse(null);
  }

  @Transactional
  public ItemDTO createItem(ItemCreateDTO createDTO) {
	Item item = mapper.toEntity(createDTO);
	return mapper.toDto(repository.save(item));
  }

  @Transactional
  public ItemDTO updateItem(Long id, ItemUpdateDTO updateDto) {
	return repository.findById(id)
		.map(item -> {
		  item.setName(updateDto.getName());
		  item.setDescription(updateDto.getDescription());
		  if (updateDto.getCompleted() != null) {
			item.setCompleted(updateDto.getCompleted());
		  }
		  return mapper.toDto(repository.save(item));
		})
		.orElse(null);
  }

  @Transactional
  public boolean deleteItem(Long id) {
	if (repository.existsById(id)) {
	  repository.deleteById(id);
	  return true;
	}
	return false;
  }
  public Item getItemEntityById(Long id) {
	return repository.findById(id)
		.orElseThrow(() -> new NoSuchElementException("Задача с ID " + id + " не найдена"));
  }

   @Transactional
  public void addTagToItem(Long itemId, Integer tagId) {
	Item item = getItemEntityById(itemId);
	Tag tag = tagRepository.findById(tagId)
		.orElseThrow(() -> new NoSuchElementException("Тег с ID " + tagId + " не найден"));
	item.getTags().add(tag);
	repository.save(item);
  }

  @Transactional
  public void removeTagFromItem(Long itemId, Integer tagId) {
	Item item = getItemEntityById(itemId);
	item.getTags().removeIf(tag -> tag.getId().equals(tagId));
	repository.save(item);
  }
}