package com.example.planner.cache;

import com.example.planner.dto.user.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UserCache {

  private final ConcurrentHashMap<UserQueryKey, Page<UserDTO>> cache = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
	log.info("PhotoSessionCache инициализирован с HashMap индексом");
  }

  public Page<UserDTO> get(UserQueryKey key) {
	Page<UserDTO> result = cache.get(key);
	if (result != null) {
	  log.info("Кэш HIT для ключа: {}", key);
	} else {
	  log.info("Кэш MISS для ключа: {}", key);
	}
	return result;
  }

  public void put(UserQueryKey key, Page<UserDTO> data) {
	cache.put(key, data);
	log.info("Данные закэшированы для ключа: {}", key);
  }

  public void invalidateByGroupName(String groupName) {
	int removedCount = 0;
	for (UserQueryKey key : cache.keySet()) {
	  if (key.groupName() != null && key.groupName().equals(groupName)) {
		cache.remove(key);
		removedCount++;
	  }
	}
	log.info("Кэш очищен для имени группы: {}, удалено записей: {}", groupName, removedCount);
  }

  public void invalidateByRoleName(String roleName) {
	int removedCount = 0;
	for (UserQueryKey key : cache.keySet()) {
	  if (key.roleName() != null && key.roleName().equals(roleName)) {
		cache.remove(key);
		removedCount++;
	  }
	}
	log.info("Кэш очищен для роли: {}, удалено записей: {}", roleName, removedCount);
  }

  public void invalidateAll() {
	int size = cache.size();
	cache.clear();
	log.info("Кэш полностью очищен, удалено записей: {}", size);
  }

  public int getCacheSize() {
	return cache.size();
  }
}