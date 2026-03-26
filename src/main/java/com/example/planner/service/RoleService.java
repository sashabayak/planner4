package com.example.planner.service;

import com.example.planner.dto.role.RoleCreateDTO;
import com.example.planner.dto.role.RoleDTO;
import com.example.planner.dto.role.RoleUpdateDTO;
import com.example.planner.entity.Role;
import com.example.planner.mapper.RoleMapper;
import com.example.planner.repository.RoleRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository repository;
  private final RoleMapper mapper;

  public List<RoleDTO> getAllRoles() {
	return repository.findAll()
		.stream()
		.map(mapper::toDto)
		.toList();
  }

  public RoleDTO getRoleById(Long id) {
	return repository.findById(id)
		.map(mapper::toDto)
		.orElse(null);
  }

  @Transactional
  public RoleDTO createRole(RoleCreateDTO createDTO) {
	Role role = mapper.toEntity(createDTO);
	return mapper.toDto(repository.save(role));
  }

  @Transactional
  public RoleDTO updateRole(Long id, RoleUpdateDTO updateDTO) {
	return repository.findById(id)
		.map(role -> {
		  role.setName(updateDTO.getName());
		  return mapper.toDto(repository.save(role));
		})
		.orElse(null);
  }

  @Transactional
  public boolean deleteRole(Long id) {
	if (repository.existsById(id)) {
	  repository.deleteById(id);
	  return true;
	}
	return false;
  }
}
