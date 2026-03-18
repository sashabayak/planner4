package com.example.planner.service;

import com.example.planner.entity.Group;
import com.example.planner.dto.group.GroupCreateDTO;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.group.GroupUpdateDTO;
import com.example.planner.mapper.GroupMapper;
import com.example.planner.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
  private final GroupRepository repository;
  private final GroupMapper mapper;

  public List<GroupDTO> getAllGroups() {
    return repository.findAll()
        .stream()
        .map(mapper::toDto)
        .toList();
  }
  public GroupDTO getGroupById(Long id) {
    return repository.findById(id)
        .map(mapper::toDto)
        .orElse(null);
  }
  @Transactional
  public GroupDTO createGroup(GroupCreateDTO createDTO) {
    Group group = mapper.toEntity(createDTO);
	return mapper.toDto(repository.save(group));
  }
  @Transactional
  public GroupDTO updateGroup(Long id, GroupUpdateDTO updateDto) {
    return repository.findById(id)
        .map(client -> {
          client.setName(updateDto.getName());
          return mapper.toDto(repository.save(client));
        })
        .orElse(null);
  }

  @Transactional
  public boolean deleteGroup(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }
}