package com.example.planner.service;

import java.util.List;

import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.dto.item.ItemUpdateDTO;
import com.example.planner.entity.Item;
import com.example.planner.mapper.ItemMapper;
import com.example.planner.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository repository;
  private final ItemMapper mapper;

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
}