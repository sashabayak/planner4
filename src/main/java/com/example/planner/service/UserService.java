package com.example.planner.service;

import com.example.planner.dto.UserDTO;
import com.example.planner.entity.User;
import com.example.planner.mapper.UserMapper;
import com.example.planner.repository.UserRepository;
import com.example.planner.repository.GroupRepository;
import com.example.planner.entity.Item;
import com.example.planner.repository.ItemRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.planner.entity.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;
  private final GroupRepository groupRepository;
  private final ItemRepository itemRepository;
  public List<UserDTO> findAll() {
	return repository.findAll().stream().map(UserMapper::toDto).toList();
  }

  public List<UserDTO> findAllWithNPlusOne() {
	List<User> users = repository.findAll();
	users.forEach(user -> user.getItems().size());
	return users.stream().map(UserMapper::toDto).toList();
  }

  public List<UserDTO> findAllWithoutNPlusOne() {
	return repository.findAllWithItems().stream().map(UserMapper::toDto).toList();
  }

  public Optional<UserDTO> findById(Integer id) {
	return repository.findById(id).map(UserMapper::toDto);
  }

  public UserDTO save(UserDTO dto) {
	User user = UserMapper.toEntity(dto);

	if (dto.getGroupId() != null) {
	  Group group = groupRepository.findById(dto.getGroupId())
		  .orElseThrow(() -> new RuntimeException("Group not found"));
	  user.setGroup(group);
	}

	User savedUser = repository.save(user);
	return UserMapper.toDto(savedUser);
  }
  public Optional<UserDTO> update(Integer id, UserDTO dto) {
	return repository.findById(id)
		.map(user -> {
		  user.setName(dto.getName());
		  user.setBirthDate(dto.getBirthDate());
		  return UserMapper.toDto(repository.save(user));
		});
  }
  public void deleteById(Integer id) {
	repository.deleteById(id);
  }

  @Transactional
  public void addItemToUser(Integer userId, Integer itemId) {
	User user = repository.findById(userId)
		.orElseThrow(() -> new RuntimeException("User not found"));
	Item item = itemRepository.findById(itemId)
		.orElseThrow(() -> new RuntimeException("Item not found"));

	user.getItems().add(item);
	repository.save(user);
  }
}
