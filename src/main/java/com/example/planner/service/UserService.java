package com.example.planner.service;

import com.example.planner.cache.UserCache;
import com.example.planner.cache.UserQueryKey;
import com.example.planner.dto.user.UserCreateDTO;
import com.example.planner.dto.user.UserDTO;
import com.example.planner.dto.user.UserFilterDTO;
import com.example.planner.dto.user.UserUpdateDTO;
import com.example.planner.entity.Group;
import com.example.planner.entity.Role;
import com.example.planner.entity.User;
import com.example.planner.mapper.UserMapper;
import com.example.planner.repository.GroupRepository;
import com.example.planner.repository.RoleRepository;
import com.example.planner.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private static final String GROUP_NOT_FOUND = "Группа не найдена";
  private static final String ROLE_NOT_FOUND = "Роль не найдена";
  private static final String WITH_ID = " с id: ";
  private static final String TX_DEMO_START = "ТРАНЗАКЦИЯ ДЕМО - СТАРТ";
  private static final String TX_DEMO_END_COMMITTED = "ТРАНЗАКЦИЯ ДЕМО - КОНЕЦ (зафиксировано)";
  private static final String NO_TX_DEMO_START = "БЕЗ ТРАНЗАКЦИИ ДЕМО - СТАРТ";
  private static final String NO_TX_DEMO_END_SUCCESS = "БЕЗ ТРАНЗАКЦИИ ДЕМО - КОНЕЦ (успешно)";
  private static final String N_PLUS_1_DEMO = "N+1 ПРОБЛЕМА ДЕМО";
  private static final String ENTITY_GRAPH_DEMO = "ENTITYGRAPH РЕШЕНИЕ ДЕМО";

  private final UserRepository repository;
  private final UserMapper mapper;
  private final GroupRepository groupRepository;
  private final RoleRepository roleRepository;
  private final UserCache userCache;


  public List<UserDTO> getAllUsers() {
	LOG.info("Получение всех пользователей");
	return repository.findAll()
		.stream()
		.map(mapper::toDto)
		.toList();
  }

  public UserDTO getUserById(Long id) {
	LOG.info("Получение пользователя по ID: {}", id);
	return repository.findById(id)
		.map(mapper::toDto)
		.orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + id + " не найден"));  }

  public List<UserDTO> getUsersByGroupId(Long groupId) {
	LOG.info("Получение пользователей по ID группы: {}", groupId);
	return repository.findByGroupId(groupId)
		.stream()
		.map(mapper::toDto)
		.toList();
  }

  @Transactional
  public UserDTO createUser(UserCreateDTO createDTO) {
	LOG.info("Создание нового пользователя");

	Group group = findGroupById(createDTO.getGroupId());
	Role role = findRoleById(createDTO.getRoleId());

	User user = mapper.toEntity(createDTO, role, group);

	UserDTO saved = mapper.toDto(repository.save(user));
	userCache.invalidateAll();
	LOG.info("Кэш очищен после создания пользователя с ID: {}", saved.getId());

	return saved;
  }

  @Transactional
  public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
	LOG.info("Обновление пользователя с ID: {}", id);

	return repository.findById(id)
		.map(user -> {
		  if (updateDTO.getName() != null) {
			user.setName(updateDTO.getName());
		  }
		  if (updateDTO.getBirthDate() != null) {
			user.setBirthDate(updateDTO.getBirthDate());
		  }
		  if (updateDTO.getGroupId() != null) {
			Group group = findGroupById(updateDTO.getGroupId());
			user.setGroup(group);
		  }
		  if (updateDTO.getRoleId() != null) {
			Role role = findRoleById(updateDTO.getRoleId());
			user.setRole(role);
		  }

		  UserDTO updated = mapper.toDto(repository.save(user));
		  userCache.invalidateAll();
		  LOG.info("Кэш очищен после обновления пользователя с ID: {}", id);

		  return updated;
		})
		.orElse(null);
  }

  @Transactional
  public boolean deleteUser(Long id) {
	LOG.info("Удаление пользователя с ID: {}", id);

	return repository.findById(id)
		.map(user -> {
		  String groupName = user.getGroup() != null ? user.getGroup().getName() : null;
		  String roleName = user.getRole() != null ? user.getRole().getName() : null;

		  repository.delete(user);

		  if (groupName != null) {
			userCache.invalidateByGroupName(groupName);
		  }
		  if (roleName != null) {
			userCache.invalidateByRoleName(roleName);
		  }

		  LOG.info("Кэш очищен после удаления пользователя с ID: {}", id);
		  return true;
		})
		.orElse(false);
  }

  public void demonstrateNPlus1Problem() {
	demonstrateFetchProblem(N_PLUS_1_DEMO, false);
  }

  public void demonstrateEntityGraphSolution() {
	demonstrateFetchProblem(ENTITY_GRAPH_DEMO, true);
  }

  @Transactional
  public void createWithRelatedWithTransaction(UserCreateDTO dto) {
	processUserCreation(dto, "С ТРАНЗАКЦИЕЙ", true);
  }

  public void createWithRelatedWithoutTransaction(UserCreateDTO dto) {
	processUserCreation(dto, "БЕЗ ТРАНЗАКЦИИ", false);
  }

  public List<UserDTO> getUserWithFiltersJpql(UserFilterDTO filter) {
	LOG.info("Поиск пользователей с фильтрацией JPQL");

	List<User> users = repository.findUsersWithFiltersJpql(
		filter.groupName(),
		filter.roleName()
	);

	LOG.info("Найдено пользователей: {}", users.size());
	return users.stream()
		.map(mapper::toDto)
		.toList();
  }

  public List<UserDTO> getUserWithFiltersNative(UserFilterDTO filter) {
	LOG.info("Поиск пользователей с фильтрацией (Native Query)");

	List<User> users = repository.findUsersWithFiltersNative(
		filter.roleName(),
		filter.groupName()
	);

	LOG.info("Найдено пользователей: {}", users.size());
	return users.stream()
		.map(mapper::toDto)
		.toList();
  }

  public Page<UserDTO> getUsersWithFiltersPaged(UserFilterDTO filter) {
	LOG.info("Поиск пользователей с пагинацией: page={}, size={}", filter.page(), filter.size());

	Pageable pageable = filter.toPageable();

	Page<User> userPage = repository.findUsersWithFiltersPaged(
		filter.groupName(),
		filter.roleName(),
		pageable
	);

	LOG.info("Найдено пользователей: {}, всего страниц: {}",
		userPage.getNumberOfElements(),
		userPage.getTotalPages());

	return userPage.map(mapper::toDto);
  }

  public Page<UserDTO> getUsersWithCache(UserFilterDTO filter) {
	LOG.info("Поиск пользователей с использованием кэша: page={}, size={}",
		filter.page(), filter.size());

	UserQueryKey cacheKey = new UserQueryKey(
		filter.groupName(),
		filter.roleName(),
		filter.page(),
		filter.size()
	);

	Page<UserDTO> cachedResult = userCache.get(cacheKey);
	if (cachedResult != null) {
	  LOG.info("Данные получены из кэша");
	  return cachedResult;
	}

	LOG.info("Данных нет в кэше, выполняем запрос к БД");
	Page<UserDTO> result = getUsersWithFiltersPaged(filter);
	userCache.put(cacheKey, result);

	return result;
  }

  public int getCacheSize() {
	int size = userCache.getCacheSize();
	LOG.info("Текущий размер кэша: {}", size);
	return size;
  }

  private void demonstrateFetchProblem(String demoType, boolean useEntityGraph) {
	LOG.info("{} - СТАРТ", demoType);

	List<User> users = useEntityGraph
		? repository.findAllWithEntityGraph()
		: repository.findAllWithoutFetch();

	LOG.info("Загружено пользователей: {}", users.size());

	int queryNumber = 1;
	for (User user : users) {
	  if (!useEntityGraph) {
		queryNumber++;
	  }

	  String groupName = user.getGroup() != null ? user.getGroup().getName() : "null";
	  String roleName = user.getRole() != null ? user.getRole().getName() : "null";

	  if (useEntityGraph) {
		LOG.info("Пользователь {}: группа={}, роль={} (все загружено в основном запросе)",
			user.getId(), groupName, roleName);
	  } else {
		LOG.info("Запрос {}: Пользователь {}: группа={}, роль={}",
			queryNumber, user.getId(), groupName, roleName);
	  }
	}

	String totalQueries = useEntityGraph
		? "всего запросов: 1"
		: "всего запросов: 1 + " + users.size();
	LOG.info("{} - КОНЕЦ ({})", demoType, totalQueries);
  }

  private void processUserCreation(UserCreateDTO dto, String type, boolean isTransactional) {
	String startMessage = isTransactional ? TX_DEMO_START : NO_TX_DEMO_START;
	LOG.info(startMessage);

	Group group = findGroupById(dto.getGroupId());
	Role role = findRoleById(dto.getRoleId());

	User user = buildUser(dto, group, role);
	User saved = repository.save(user);

	LOG.info("Сохранен пользователь id={} имя={} ({})", saved.getId(), saved.getName(), type);

	if (dto.getGroupId() == 3) {
	  String errorMessage = "Демонстрационная ошибка: группа с ID=3 запрещена";
	  LOG.error("{} ДЕМО - ОШИБКА: {}", type, errorMessage);
	  throw new IllegalStateException(errorMessage);
	}

	String endMessage = isTransactional ? TX_DEMO_END_COMMITTED : NO_TX_DEMO_END_SUCCESS;
	LOG.info(endMessage);
  }

  private Group findGroupById(Long id) {
	return groupRepository.findById(id)
		.orElseThrow(() -> new NoSuchElementException(GROUP_NOT_FOUND+WITH_ID+ id));
  }


  private Role findRoleById(Long id) {
	return roleRepository.findById(id)
		.orElseThrow(() -> new NoSuchElementException(ROLE_NOT_FOUND + WITH_ID + id));
  }

  private User buildUser(UserCreateDTO dto, Group group, Role role) {
	return User.builder()
		.name(dto.getName())
		.birthDate(dto.getBirthDate())
		.group(group)
		.role(role)
		.build();
  }
}
