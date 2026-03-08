// src/main/java/com/example/planner/service/UserService.java
package com.example.planner.service;

import com.example.planner.dto.UserDTO;
import com.example.planner.entity.User;
import com.example.planner.mapper.UserMapper;
import com.example.planner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;

  public List<UserDTO> findAll() {
	return repository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
  }

  public List<UserDTO> findAllWithNPlusOne() {
	List<User> users = repository.findAll();
	users.forEach(user -> user.getItems().size());
	return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
  }

  public List<UserDTO> findAllWithoutNPlusOne() {
	return repository.findAllWithItems().stream().map(UserMapper::toDto).collect(Collectors.toList());
  }

  public Optional<UserDTO> findById(Integer id) {
	return repository.findById(id).map(UserMapper::toDto);
  }

  public UserDTO save(UserDTO dto) {
	User user = UserMapper.toEntity(dto);
	return UserMapper.toDto(repository.save(user));
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
}