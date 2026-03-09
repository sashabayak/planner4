package com.example.planner.service;

import com.example.planner.dto.ItemDTO;
import com.example.planner.entity.Item;
import com.example.planner.mapper.ItemMapper;
import com.example.planner.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository repository;

  public List<ItemDTO> findAll() {
	return repository.findAll().stream().map(ItemMapper::toDto).collect(Collectors.toList());
  }

  public Optional<ItemDTO> findById(Integer id) {
	return repository.findById(id).map(ItemMapper::toDto);
  }

  public ItemDTO save(ItemDTO dto) {
	Item item = ItemMapper.toEntity(dto);
	return ItemMapper.toDto(repository.save(item));
  }

  public void deleteById(Integer id) {
	repository.deleteById(id);
  }

  public List<ItemDTO> searchByName(String name) {
	String keyword = (name == null || name.trim().isEmpty()) ? "" : name;
	return repository.findByNameContainingIgnoreCase(keyword)
		.stream().map(ItemMapper::toDto).collect(Collectors.toList());
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
}