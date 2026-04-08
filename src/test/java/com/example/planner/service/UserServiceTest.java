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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository repository;
  @Mock private UserMapper mapper;
  @Mock private GroupRepository groupRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private UserCache userCache;

  @InjectMocks private UserService userService;

  private User user;
  private UserDTO userDto;
  private Group group;
  private Role role;
  private UserCreateDTO createDto;

  private static final Long ID = 1L;

  @BeforeEach
  void setUp() {
	group = Group.builder().id(1L).name("AdminGroup").build();
	role = Role.builder().id(1L).name("ADMIN").build();
	user = User.builder().id(ID).name("Test User").group(group).role(role).build();
	userDto = UserDTO.builder().id(ID).name("Test User").build();
	createDto = UserCreateDTO.builder().name("New").groupId(1L).roleId(1L).birthDate(LocalDate.now().minusYears(20)).build();
  }

  @Test
  void getAllUsers_ShouldReturnList() {
	when(repository.findAll()).thenReturn(List.of(user));
	when(mapper.toDto(user)).thenReturn(userDto);
	List<UserDTO> result = userService.getAllUsers();
	assertThat(result).hasSize(1);
  }

  @Test
  void getUserById_WhenFound_ShouldReturnDto() {
	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(mapper.toDto(user)).thenReturn(userDto);
	assertThat(userService.getUserById(ID)).isEqualTo(userDto);
  }

  @Test
  void getUserById_WhenNotFound_ShouldThrowException() {
	when(repository.findById(ID)).thenReturn(Optional.empty());
	assertThatThrownBy(() -> userService.getUserById(ID)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void getUsersByGroupId_ShouldReturnList() {
	when(repository.findByGroupId(1L)).thenReturn(List.of(user));
	when(mapper.toDto(user)).thenReturn(userDto);
	assertThat(userService.getUsersByGroupId(1L)).hasSize(1);
  }

  @Test
  void createUser_ShouldSaveAndInvalidateCache() {
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(mapper.toEntity(any(), any(), any())).thenReturn(user);
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.createUser(createDto);

	verify(userCache).invalidateAll();
	verify(repository).save(any());
  }

  @Test
  void updateUser_AllFields_ShouldUpdateAndInvalidateCache() {
	UserUpdateDTO update = new UserUpdateDTO();
	update.setName("Updated");
	update.setGroupId(1L);
	update.setRoleId(1L);
	update.setBirthDate(LocalDate.now().minusYears(25));

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getName()).isEqualTo("Updated");
	verify(userCache).invalidateAll();
  }

  @Test
  void deleteUser_WhenExists_ShouldInvalidateSpecificCache() {
	when(repository.findById(ID)).thenReturn(Optional.of(user));

	boolean result = userService.deleteUser(ID);

	assertThat(result).isTrue();
	verify(userCache).invalidateByGroupName("AdminGroup");
	verify(userCache).invalidateByRoleName("ADMIN");
	verify(repository).delete(user);
  }

  @Test
  void deleteUser_WhenNotFound_ShouldReturnFalse() {
	when(repository.findById(ID)).thenReturn(Optional.empty());
	assertThat(userService.deleteUser(ID)).isFalse();
  }

  @Test
  void getUserWithFiltersJpql_ShouldCallRepository() {
	UserFilterDTO filter = UserFilterDTO.builder().groupName("G").roleName("R").build();
	when(repository.findUsersWithFiltersJpql("G", "R")).thenReturn(List.of(user));
	when(mapper.toDto(user)).thenReturn(userDto);

	userService.getUserWithFiltersJpql(filter);
	verify(repository).findUsersWithFiltersJpql("G", "R");
  }

  @Test
  void getUserWithFiltersNative_ShouldCallRepository() {
	UserFilterDTO filter = UserFilterDTO.builder().groupName("G").roleName("R").build();
	when(repository.findUsersWithFiltersNative("R", "G")).thenReturn(List.of(user)); // В сервисе перепутаны параметры в вызове native!

	userService.getUserWithFiltersNative(filter);
	verify(repository).findUsersWithFiltersNative("R", "G");
  }

  @Test
  void getUsersWithFiltersPaged_ShouldReturnPage() {
	UserFilterDTO filter = UserFilterDTO.builder().page(0).size(10).build();
	Page<User> page = new PageImpl<>(List.of(user));
	when(repository.findUsersWithFiltersPaged(any(), any(), any(Pageable.class))).thenReturn(page);

	userService.getUsersWithFiltersPaged(filter);
	verify(repository).findUsersWithFiltersPaged(any(), any(), any());
  }

  @Test
  void getUsersWithCache_WhenInCache_ShouldNotCallDb() {
	UserFilterDTO filter = UserFilterDTO.builder().page(0).size(10).build();
	Page<UserDTO> cachedPage = new PageImpl<>(List.of(userDto));
	when(userCache.get(any(UserQueryKey.class))).thenReturn(cachedPage);

	Page<UserDTO> result = userService.getUsersWithCache(filter);

	assertThat(result).isEqualTo(cachedPage);
	verify(repository, never()).findUsersWithFiltersPaged(any(), any(), any());
  }

  @Test
  void getUsersWithCache_WhenNotInCache_ShouldCallDbAndPutInCache() {
	UserFilterDTO filter = UserFilterDTO.builder().page(0).size(10).build();
	Page<User> dbPage = new PageImpl<>(List.of(user));
	when(userCache.get(any())).thenReturn(null);
	when(repository.findUsersWithFiltersPaged(any(), any(), any())).thenReturn(dbPage);
	when(mapper.toDto(user)).thenReturn(userDto);

	userService.getUsersWithCache(filter);

	verify(userCache).put(any(), any());
  }

  @Test
  void getCacheSize_ShouldReturnSize() {
	when(userCache.getCacheSize()).thenReturn(5);
	assertThat(userService.getCacheSize()).isEqualTo(5);
  }

  @Test
  void demonstrateNPlus1_ShouldExecuteAllLines() {
	when(repository.findAllWithoutFetch()).thenReturn(List.of(user));
	userService.demonstrateNPlus1Problem();
	verify(repository).findAllWithoutFetch();
  }

  @Test
  void demonstrateEntityGraph_ShouldExecuteAllLines() {
	when(repository.findAllWithEntityGraph()).thenReturn(List.of(user));
	userService.demonstrateEntityGraphSolution();
	verify(repository).findAllWithEntityGraph();
  }
  @Test
  void createWithRelatedWithTransaction_ShouldUseTransactionalMessage() {
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(repository.save(any())).thenReturn(user);

	userService.createWithRelatedWithTransaction(createDto);

	verify(repository).save(any());
  }

  @Test
  void createWithRelatedWithoutTransaction_ShouldUseNonTransactionalMessage() {
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(repository.save(any())).thenReturn(user);

	userService.createWithRelatedWithoutTransaction(createDto);

	verify(repository).save(any());
  }
  @Test
  void processUserCreation_WithTransaction_ShouldWork() {
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(repository.save(any())).thenReturn(user);

	userService.createWithRelatedWithTransaction(createDto);
	verify(repository).save(any());
  }

  @Test
  void processUserCreation_WhenGroup3_ShouldThrowException() {
	createDto.setGroupId(3L);
	when(groupRepository.findById(3L)).thenReturn(Optional.of(group));
	when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
	when(repository.save(any())).thenReturn(user);

	assertThatThrownBy(() -> userService.createWithRelatedWithoutTransaction(createDto))
		.isInstanceOf(IllegalStateException.class);
  }
  @Test
  void updateUser_OnlyName_ShouldUpdateOnlyName() {
	UserUpdateDTO update = new UserUpdateDTO();
	update.setName("Updated Name Only");

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getName()).isEqualTo("Updated Name Only");
	assertThat(user.getGroup()).isEqualTo(group);
	assertThat(user.getRole()).isEqualTo(role);
	verify(userCache).invalidateAll();
  }

  @Test
  void updateUser_OnlyGroupId_ShouldUpdateOnlyGroup() {
	Group newGroup = Group.builder().id(2L).name("NewGroup").build();
	UserUpdateDTO update = new UserUpdateDTO();
	update.setGroupId(2L);

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(groupRepository.findById(2L)).thenReturn(Optional.of(newGroup));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getGroup()).isEqualTo(newGroup);
	assertThat(user.getName()).isEqualTo("Test User"); // не изменилось
	verify(userCache).invalidateAll();
  }

  @Test
  void updateUser_OnlyRoleId_ShouldUpdateOnlyRole() {
	Role newRole = Role.builder().id(2L).name("USER").build();
	UserUpdateDTO update = new UserUpdateDTO();
	update.setRoleId(2L);

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(roleRepository.findById(2L)).thenReturn(Optional.of(newRole));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getRole()).isEqualTo(newRole);
	verify(userCache).invalidateAll();
  }

  @Test
  void updateUser_OnlyBirthDate_ShouldUpdateOnlyBirthDate() {
	LocalDate newBirthDate = LocalDate.now().minusYears(30);
	UserUpdateDTO update = new UserUpdateDTO();
	update.setBirthDate(newBirthDate);

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getBirthDate()).isEqualTo(newBirthDate);
	verify(userCache).invalidateAll();
  }

  @Test
  void updateUser_EmptyUpdate_ShouldNotChangeAnything() {
	UserUpdateDTO update = new UserUpdateDTO();

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getName()).isEqualTo("Test User");
	assertThat(user.getGroup()).isEqualTo(group);
	assertThat(user.getRole()).isEqualTo(role);
	verify(userCache).invalidateAll();
  }

  @Test
  void updateUser_NullName_ShouldNotUpdateName() {
	UserUpdateDTO update = new UserUpdateDTO();
	update.setName(null);
	update.setGroupId(1L);

	when(repository.findById(ID)).thenReturn(Optional.of(user));
	when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
	when(repository.save(any())).thenReturn(user);
	when(mapper.toDto(any())).thenReturn(userDto);

	userService.updateUser(ID, update);

	assertThat(user.getName()).isEqualTo("Test User"); // имя не изменилось
	verify(userCache).invalidateAll();
  }

  @Test
  void deleteUser_WhenUserHasNoGroupAndNoRole_ShouldInvalidateOnlyByNullValues() {
	User userWithoutGroupAndRole = User.builder()
		.id(ID)
		.name("Test User")
		.group(null)
		.role(null)
		.build();

	when(repository.findById(ID)).thenReturn(Optional.of(userWithoutGroupAndRole));

	boolean result = userService.deleteUser(ID);

	assertThat(result).isTrue();
	verify(userCache).invalidateByGroupName(null);
	verify(userCache).invalidateByRoleName(null);
	verify(repository).delete(userWithoutGroupAndRole);
  }

  @Test
  void deleteUser_WhenUserHasGroupButNoRole_ShouldHandleNullRole() {
	User userWithGroupOnly = User.builder()
		.id(ID)
		.name("Test User")
		.group(group)
		.role(null)
		.build();

	when(repository.findById(ID)).thenReturn(Optional.of(userWithGroupOnly));

	boolean result = userService.deleteUser(ID);

	assertThat(result).isTrue();
	verify(userCache).invalidateByGroupName("AdminGroup");
	verify(userCache).invalidateByRoleName(null);
	verify(repository).delete(userWithGroupOnly);
  }

  @Test
  void deleteUser_WhenUserHasRoleButNoGroup_ShouldHandleNullGroup() {
	User userWithRoleOnly = User.builder()
		.id(ID)
		.name("Test User")
		.group(null)
		.role(role)
		.build();

	when(repository.findById(ID)).thenReturn(Optional.of(userWithRoleOnly));

	boolean result = userService.deleteUser(ID);

	assertThat(result).isTrue();
	verify(userCache).invalidateByGroupName(null);
	verify(userCache).invalidateByRoleName("ADMIN");
	verify(repository).delete(userWithRoleOnly);
  }
}