package com.example.planner.service;

import com.example.planner.dto.group.GroupCreateDTO;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.group.GroupUpdateDTO;
import com.example.planner.entity.Group;
import com.example.planner.mapper.GroupMapper;
import com.example.planner.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  private static final String GROUP_NAME = "Администраторы";
  private static final String UPDATED_NAME = "Разработчики";
  private static final long ID = 1L;
  private static final long NON_EXISTENT_ID = 999L;

  @Mock
  private GroupRepository groupRepository;

  @Mock
  private GroupMapper groupMapper;

  @InjectMocks
  private GroupService groupService;

  private Group group;
  private GroupDTO groupDto;
  private GroupCreateDTO groupCreateDto;
  private GroupUpdateDTO groupUpdateDto;

  @BeforeEach
  void setUp() {
	groupCreateDto = new GroupCreateDTO();
	groupCreateDto.setName(GROUP_NAME);

	groupUpdateDto = new GroupUpdateDTO();
	groupUpdateDto.setName(UPDATED_NAME);

	group = Group.builder()
		.id(ID)
		.name(GROUP_NAME)
		.build();

	groupDto = new GroupDTO();
	groupDto.setId(ID);
	groupDto.setName(GROUP_NAME);
  }

  @Test
  void getAllGroupsShouldReturnListOfGroups() {
	List<Group> groups = List.of(group);

	when(groupRepository.findAll()).thenReturn(groups);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	List<GroupDTO> result = groupService.getAllGroups();

	assertThat(result).hasSize(1);
	assertThat(result.get(0).getId()).isEqualTo(ID);
	assertThat(result.get(0).getName()).isEqualTo(GROUP_NAME);
	verify(groupRepository, times(1)).findAll();
  }

  @Test
  void getAllGroupsWhenNoGroupsShouldReturnEmptyList() {
	when(groupRepository.findAll()).thenReturn(List.of());

	List<GroupDTO> result = groupService.getAllGroups();

	assertThat(result).isEmpty();
	verify(groupRepository, times(1)).findAll();
  }

  @Test
  void getGroupByIdWhenGroupExistsShouldReturnGroup() {
	when(groupRepository.findById(ID)).thenReturn(Optional.of(group));
	when(groupMapper.toDto(group)).thenReturn(groupDto);

	GroupDTO result = groupService.getGroupById(ID);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	assertThat(result.getName()).isEqualTo(GROUP_NAME);
	verify(groupRepository, times(1)).findById(ID);
  }

  @Test
  void getGroupByIdWhenGroupNotExistsShouldReturnNull() {
	when(groupRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

	GroupDTO result = groupService.getGroupById(NON_EXISTENT_ID);

	assertThat(result).isNull();
	verify(groupRepository, times(1)).findById(NON_EXISTENT_ID);
  }

  @Test
  void createGroupShouldReturnCreatedGroup() {
	when(groupRepository.save(any(Group.class))).thenReturn(group);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	GroupDTO result = groupService.createGroup(groupCreateDto);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	assertThat(result.getName()).isEqualTo(GROUP_NAME);
	verify(groupRepository, times(1)).save(any(Group.class));
  }

  @Test
  void createGroupShouldSaveGroupWithCorrectData() {
	when(groupRepository.save(any(Group.class))).thenReturn(group);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	groupService.createGroup(groupCreateDto);

	verify(groupRepository).save(any(Group.class));
  }

  @Test
  void updateGroupWhenGroupExistsShouldReturnUpdatedGroup() {
	Group updatedGroup = Group.builder()
		.id(ID)
		.name(UPDATED_NAME)
		.build();

	GroupDTO updatedDto = GroupDTO.builder()
		.id(ID)
		.name(UPDATED_NAME)
		.build();

	when(groupRepository.findById(ID)).thenReturn(Optional.of(group));
	when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);
	when(groupMapper.toDto(updatedGroup)).thenReturn(updatedDto);

	GroupDTO result = groupService.updateGroup(ID, groupUpdateDto);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	assertThat(result.getName()).isEqualTo(UPDATED_NAME);
	verify(groupRepository, times(1)).save(any(Group.class));
  }



  @Test
  void updateGroupWhenGroupNotExistsShouldReturnNull() {
	when(groupRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

	GroupDTO result = groupService.updateGroup(NON_EXISTENT_ID, groupUpdateDto);

	assertThat(result).isNull();
	verify(groupRepository, never()).save(any(Group.class));
  }

  @Test
  void deleteGroupWhenGroupExistsShouldReturnTrue() {
	when(groupRepository.existsById(ID)).thenReturn(true);
	doNothing().when(groupRepository).deleteById(ID);

	boolean result = groupService.deleteGroup(ID);

	assertThat(result).isTrue();
	verify(groupRepository, times(1)).existsById(ID);
	verify(groupRepository, times(1)).deleteById(ID);
  }

  @Test
  void deleteGroupWhenGroupNotExistsShouldReturnFalse() {
	when(groupRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

	boolean result = groupService.deleteGroup(NON_EXISTENT_ID);

	assertThat(result).isFalse();
	verify(groupRepository, times(1)).existsById(NON_EXISTENT_ID);
	verify(groupRepository, never()).deleteById(anyLong());
  }

  @Test
  void createGroupsBulkShouldReturnListOfCreatedGroups() {
	List<GroupCreateDTO> createDtos = Arrays.asList(groupCreateDto, groupCreateDto);
	List<Group> groups = Arrays.asList(group, group);

	when(groupRepository.saveAll(anyList())).thenReturn(groups);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	List<GroupDTO> result = groupService.createGroupsBulk(createDtos);

	assertThat(result).hasSize(2);
	verify(groupRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createGroupsBulkWithEmptyListShouldReturnEmptyList() {
	List<GroupCreateDTO> createDtos = List.of();

	when(groupRepository.saveAll(anyList())).thenReturn(List.of());

	List<GroupDTO> result = groupService.createGroupsBulk(createDtos);

	assertThat(result).isEmpty();
	verify(groupRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createGroupsBulkWithTransactionWhenLessOrEqual5ShouldSucceed() {
	List<GroupCreateDTO> createDtos = Arrays.asList(
		groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto
	);
	List<Group> groups = Arrays.asList(group, group, group, group, group);

	when(groupRepository.saveAll(anyList())).thenReturn(groups);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	List<GroupDTO> result = groupService.createGroupsBulkWithTransaction(createDtos);

	assertThat(result).hasSize(5);
	verify(groupRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createGroupsBulkWithTransactionWhenMoreThan5ShouldThrowException() {
	List<GroupCreateDTO> createDtos = Arrays.asList(
		groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto
	);
	List<Group> groups = Arrays.asList(group, group, group,group, group, group);

	when(groupRepository.saveAll(anyList())).thenReturn(groups);

	assertThatThrownBy(() -> groupService.createGroupsBulkWithTransaction(createDtos))
		.isInstanceOf(NoSuchElementException.class)
		.hasMessageContaining("Превышен лимит")
		.hasMessageContaining("Транзакция будет откачена");

	verify(groupRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createGroupsBulkWithoutTransactionWhenLessOrEqual5ShouldSucceed() {
	List<GroupCreateDTO> createDtos = Arrays.asList(
		groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto
	);
	List<Group> groups = Arrays.asList(group, group, group, group, group);

	when(groupRepository.saveAll(anyList())).thenReturn(groups);
	when(groupMapper.toDto(any(Group.class))).thenReturn(groupDto);

	List<GroupDTO> result = groupService.createGroupsBulkWithoutTransaction(createDtos);

	assertThat(result).hasSize(5);
	verify(groupRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createGroupsBulkWithoutTransactionWhenMoreThan5ShouldThrowExceptionButDataSaved() {
	List<GroupCreateDTO> createDtos = Arrays.asList(
		groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto, groupCreateDto
	);
	List<Group> groups = Arrays.asList(group, group, group, group, group, group);

	when(groupRepository.saveAll(anyList())).thenReturn(groups);

	assertThatThrownBy(() -> groupService.createGroupsBulkWithoutTransaction(createDtos))
		.isInstanceOf(NoSuchElementException.class)
		.hasMessageContaining("Превышен лимит")
		.hasMessageContaining("Данные уже сохранены");

	verify(groupRepository, times(1)).saveAll(anyList());
  }



  @Test
  void updateGroupWithAllFieldsNullShouldNotUpdateAnything() {
	GroupUpdateDTO emptyUpdate = new GroupUpdateDTO();

	when(groupRepository.findById(ID)).thenReturn(Optional.of(group));
	when(groupRepository.save(any(Group.class))).thenReturn(group);
	when(groupMapper.toDto(group)).thenReturn(groupDto);

	GroupDTO result = groupService.updateGroup(ID, emptyUpdate);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	assertThat(result.getName()).isEqualTo(GROUP_NAME);
	verify(groupRepository, times(1)).save(any(Group.class));
  }

  @Test
  void deleteGroupWhenRepositoryThrowsExceptionShouldStillReturnTrue() {
	when(groupRepository.existsById(ID)).thenReturn(true);
	doNothing().when(groupRepository).deleteById(ID);

	boolean result = groupService.deleteGroup(ID);

	assertThat(result).isTrue();
	verify(groupRepository, times(1)).deleteById(ID);
  }

  @Test
  void createGroup_WithNullFields_ShouldHandleGracefully() {
	GroupCreateDTO dtoWithNulls = new GroupCreateDTO();
	dtoWithNulls.setName(null);

	Group groupWithNulls = Group.builder()
		.id(ID)
		.name(null)
		.build();

	GroupDTO dtoWithNullsResult = new GroupDTO();
	dtoWithNullsResult.setId(ID);
	dtoWithNullsResult.setName(null);

	when(groupRepository.save(any(Group.class))).thenReturn(groupWithNulls);
	when(groupMapper.toDto(any(Group.class))).thenReturn(dtoWithNullsResult);

	GroupDTO result = groupService.createGroup(dtoWithNulls);

	assertThat(result).isNotNull();
	assertThat(result.getName()).isNull();
	verify(groupRepository, times(1)).save(any(Group.class));
  }

}