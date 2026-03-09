package com.example.planner.service;

import com.example.planner.dto.RoleDTO;
import com.example.planner.entity.Role;
import com.example.planner.mapper.RoleMapper;
import com.example.planner.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository repository;

  public List<RoleDTO> findAll() {
	return repository.findAll().stream().map(RoleMapper::toDto).toList();
  }

  public Optional<RoleDTO> findById(Integer id) {
	return repository.findById(id).map(RoleMapper::toDto);
  }

  public RoleDTO save(RoleDTO dto) {
	Role role = RoleMapper.toEntity(dto);
	return RoleMapper.toDto(repository.save(role));
  }

  public void deleteById(Integer id) {
	repository.deleteById(id);
  }

  public Optional<RoleDTO> update(Integer id, RoleDTO dto) {
	return repository.findById(id)
		.map(role -> {
		  role.setName(dto.getName());
		  return RoleMapper.toDto(repository.save(role));
		});
  }
}