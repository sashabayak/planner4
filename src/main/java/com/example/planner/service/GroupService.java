package com.example.planner.service;

import com.example.planner.dto.GroupDTO;
import com.example.planner.entity.Group;
import com.example.planner.dto.UserDTO;
import com.example.planner.entity.User;
import com.example.planner.mapper.GroupMapper;
import com.example.planner.mapper.UserMapper;
import com.example.planner.repository.GroupRepository;
import com.example.planner.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
  private final GroupRepository repository;
  private final UserRepository userRepository;

  public List<GroupDTO> findAll() {
	return repository.findAll().stream().map(GroupMapper::toDto).toList();  }

  public Optional<GroupDTO> findById(Integer id) {
	return repository.findById(id).map(GroupMapper::toDto);
  }

  public GroupDTO save(GroupDTO dto) {
	Group group = GroupMapper.toEntity(dto);
	return GroupMapper.toDto(repository.save(group));
  }

  public void deleteById(Integer id) {
	repository.deleteById(id);
  }

  @Transactional
  public void saveWithUser(GroupDTO groupDto, User user) {
	Group group = GroupMapper.toEntity(groupDto);
	repository.save(group);
	user.setGroup(group);
	userRepository.save(user);
	throw new IllegalStateException("Simulated error for rollback");
  }

  public void saveWithUserNoTx(GroupDTO groupDto, User user) {
	Group group = GroupMapper.toEntity(groupDto);
	repository.save(group);
	user.setGroup(group);
	userRepository.save(user);
	throw new RuntimeException("Simulated error without rollback");
  }

  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GroupService.class);

  @Transactional
  public void createGroupWithUserWithTransaction(GroupDTO groupDto, UserDTO userDto) {
	LOG.info("TX DEMO - START");

	Group group = GroupMapper.toEntity(groupDto);
	Group savedGroup = repository.save(group);
	LOG.info("Saved group id={} name={} (in transaction)", savedGroup.getId(), savedGroup.getName());

	User user = UserMapper.toEntity(userDto);
	user.setGroup(savedGroup);
	User savedUser = userRepository.save(user);
	LOG.info("Saved user id={} name={} (in transaction)", savedUser.getId(), savedUser.getName());

	if (savedUser.getId() == 12 || savedUser.getId() == 10 || savedUser.getId() == 15) {
	  String errorMessage = "Демонстрационная ошибка: пользователь с ID " + savedUser.getId() + " запрещен";
	  LOG.error("TX DEMO - ERROR: {}", errorMessage);
	  throw new RuntimeException(errorMessage);
	}

	LOG.info("TX DEMO - END (committed)");
  }

  public void createGroupWithUserWithoutTransaction(GroupDTO groupDto, UserDTO userDto) {
	LOG.info("NO-TX DEMO - START");

	Group group = GroupMapper.toEntity(groupDto);
	Group savedGroup = repository.save(group);
	LOG.info("Saved group id={} name={} (immediately in DB)", savedGroup.getId(), savedGroup.getName());

	User user = UserMapper.toEntity(userDto);
	user.setGroup(savedGroup);
	User savedUser = userRepository.save(user);
	LOG.info("Saved user id={} name={} (immediately in DB)", savedUser.getId(), savedUser.getName());

	if (savedUser.getId() == 5 || savedUser.getId() == 10 || savedUser.getId() == 15) {
	  String errorMessage = "Демонстрационная ошибка: пользователь с ID " + savedUser.getId() + " запрещен";
	  LOG.error("NO-TX DEMO - ERROR: {}", errorMessage);
	  throw new RuntimeException(errorMessage);
	}

	LOG.info("NO-TX DEMO - END (success)");
  }

  public Optional<GroupDTO> update(Integer id, GroupDTO dto) {
	return repository.findById(id)
		.map(group -> {
		  group.setName(dto.getName());
		  return GroupMapper.toDto(repository.save(group));
		});
  }
}