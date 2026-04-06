package com.example.planner.service;

import com.example.planner.dto.group.GroupCreateDTO;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.group.GroupUpdateDTO;
import com.example.planner.entity.Group;
import com.example.planner.mapper.GroupMapper;
import com.example.planner.repository.GroupRepository;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

  private static final int MAX_BULK_SIZE = 5;

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
        .map(group -> {
          group.setName(updateDto.getName());
          return mapper.toDto(repository.save(group));
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

  @Transactional
  public List<GroupDTO> createGroupsBulk(List<GroupCreateDTO> createDTO){
    log.info("Массовое создание {} групп", createDTO.size());
    List<Group> groups = createDTO.stream()
        .map(this::convertToEntity)
        .toList();
    List<Group> savedClients = repository.saveAll(groups);
    return savedClients.stream()
        .map(mapper::toDto)
        .toList();
  }

  private Group convertToEntity(GroupCreateDTO dto) {
    return Group.builder()
        .name(dto.getName())
        .build();
  }

  @Transactional
  public List<GroupDTO> createGroupsBulkWithTransaction(List<GroupCreateDTO> createDto) {
    log.info("Создание групп с транзакцией");

    List<Group> groups = createDto.stream()
        .map(this::convertToEntity)
        .toList();
    List<Group> savedGroups = repository.saveAll(groups);

    if (createDto.size() > MAX_BULK_SIZE) {
      throw new NoSuchElementException(
          "Превышен лимит в " + MAX_BULK_SIZE + " групп. "
              + "Транзакция будет откачена"
      );
    }

    return savedGroups.stream()
        .map(mapper::toDto)
        .toList();
  }

  public List<GroupDTO> createGroupsBulkWithoutTransaction(List<GroupCreateDTO> createDto) {
    log.info("Создание групп без транзакции");

    List<Group> groups = createDto.stream()
        .map(this::convertToEntity)
        .toList();
    List<Group> savedGroups = repository.saveAll(groups);

    if (createDto.size() > MAX_BULK_SIZE) {
      throw new NoSuchElementException(
          "Превышен лимит в " + MAX_BULK_SIZE + " групп. "
              + "Данные уже сохранены в БД"
      );
    }

    return savedGroups.stream()
        .map(mapper::toDto)
        .toList();
  }
}