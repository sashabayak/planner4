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
//  public List<GroupDTO> findAll() {
//	return repository.findAll().stream().map(GroupMapper::toDto).toList();  }
//
//  public Optional<GroupDTO> findById(Integer id) {
//	return repository.findById(id).map(GroupMapper::toDto);
//  }
//
//  public GroupDTO save(GroupDTO dto) {
//	Group group = GroupMapper.toEntity(dto);
//	return GroupMapper.toDto(repository.save(group));
//  }

//  public void deleteById(Integer id) {
//	repository.deleteById(id);
//  }
//
//  @Transactional
//  public void saveWithUser(GroupDTO groupDto, User user) {
//	Group group = GroupMapper.toEntity(groupDto);
//	repository.save(group);
//	user.setGroup(group);
//	userRepository.save(user);
//	throw new IllegalStateException("Simulated error for rollback");
//  }

//  public void saveWithUserNoTx(GroupDTO groupDto, User user) {
//	Group group = GroupMapper.toEntity(groupDto);
//	repository.save(group);
//	user.setGroup(group);
//	userRepository.save(user);
//	throw new IllegalStateException("Simulated error without rollback");
//  }

//  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GroupService.class);
//
//  @Transactional
//  public void createGroupWithUserWithTransaction(GroupDTO groupDto, UserDTO userDto) {
//	processGroupUserCreation(groupDto, userDto, "TX", true);
//  }
//
//  public void createGroupWithUserWithoutTransaction(GroupDTO groupDto, UserDTO userDto) {
//	processGroupUserCreation(groupDto, userDto, "NO-TX", false);
//  }
//
//  private void processGroupUserCreation(GroupDTO groupDto, UserDTO userDto, String type, boolean isTransactional) {
//	LOG.info("{} DEMO - START", type);
//
//	Group group = GroupMapper.toEntity(groupDto);
//	Group savedGroup = repository.save(group);
//	LOG.info("Saved group id={} name={} ({} in DB)",
//		savedGroup.getId(), savedGroup.getName(),
//		isTransactional ? "in transaction" : "immediately");
//
//	User user = UserMapper.toEntity(userDto);
//	user.setGroup(savedGroup);
//	User savedUser = userRepository.save(user);
//	LOG.info("Saved user id={} name={} ({} in DB)",
//		savedUser.getId(), savedUser.getName(),
//		isTransactional ? "in transaction" : "immediately");
//
//	if (savedUser.getId() == 12 || savedUser.getId() == 10 || savedUser.getId() == 16) {
//	  String errorMessage = "Демонстрационная ошибка: пользователь с ID " + savedUser.getId() + " запрещен";
//	  LOG.error("{} DEMO - ERROR: {}", type, errorMessage);
//	  throw new IllegalStateException(errorMessage);
//	}
//
//	String endStatus = isTransactional ? "committed" : "success";
//	LOG.info("{} DEMO - END ({})", type, endStatus);
//  }
//
//  public Optional<GroupDTO> update(Integer id, GroupDTO dto) {
//	return repository.findById(id)
//		.map(group -> {
//		  group.setName(dto.getName());
//		  return GroupMapper.toDto(repository.save(group));
//		});
//  }
//  public List<GroupDTO> findAllWithUsers() {
//	return repository.findAllWithUsers().stream()
//		.map(GroupMapper::toDto)
//		.toList();
//  }
//
//  public Optional<GroupDTO> findByIdWithUsers(Integer id) {
//	return repository.findByIdWithUsers(id)
//		.map(GroupMapper::toDto);
//  }
//  public List<GroupDTO> findAllWithoutUsers() {
//	return repository.findAll().stream()
//		.map(group -> {
//		  GroupDTO dto = GroupMapper.toDto(group);
//		  dto.setUsers(null);
//		  return dto;
//		})
//		.toList();
//  }
}