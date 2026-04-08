package com.example.planner.service;

import com.example.planner.dto.role.RoleCreateDTO;
import com.example.planner.dto.role.RoleDTO;
import com.example.planner.dto.role.RoleUpdateDTO;
import com.example.planner.entity.Role;
import com.example.planner.mapper.RoleMapper;
import com.example.planner.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

  private static final Long ID = 1L;
  private static final String ROLE_NAME = "ROLE_USER";
  private static final String UPDATED_NAME = "ROLE_ADMIN";

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private RoleMapper roleMapper;

  @InjectMocks
  private RoleService roleService;

  private Role role;
  private RoleDTO roleDto;
  private RoleCreateDTO roleCreateDto;
  private RoleUpdateDTO roleUpdateDto;

  @BeforeEach
  void setUp() {
	role = Role.builder()
		.id(ID)
		.name(ROLE_NAME)
		.build();

	roleDto = RoleDTO.builder()
		.id(ID)
		.name(ROLE_NAME)
		.build();

	roleCreateDto = RoleCreateDTO.builder()
		.name(ROLE_NAME)
		.build();

	roleUpdateDto = RoleUpdateDTO.builder()
		.name(UPDATED_NAME)
		.build();
  }

  @Test
  void getAllRoles_ShouldReturnListOfDtos() {
	when(roleRepository.findAll()).thenReturn(List.of(role));
	when(roleMapper.toDto(role)).thenReturn(roleDto);

	List<RoleDTO> result = roleService.getAllRoles();

	assertThat(result).hasSize(1);
	assertThat(result.get(0).getName()).isEqualTo(ROLE_NAME);
	verify(roleRepository).findAll();
  }

  @Test
  void getRoleById_WhenExists_ShouldReturnDto() {
	when(roleRepository.findById(ID)).thenReturn(Optional.of(role));
	when(roleMapper.toDto(role)).thenReturn(roleDto);

	RoleDTO result = roleService.getRoleById(ID);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	verify(roleRepository).findById(ID);
  }

  @Test
  void getRoleById_WhenNotExists_ShouldReturnNull() {
	when(roleRepository.findById(ID)).thenReturn(Optional.empty());

	RoleDTO result = roleService.getRoleById(ID);

	assertThat(result).isNull();
	verify(roleRepository).findById(ID);
  }

  @Test
  void createRole_ShouldReturnDto() {
	when(roleMapper.toEntity(any(RoleCreateDTO.class))).thenReturn(role);
	when(roleRepository.save(any(Role.class))).thenReturn(role);
	when(roleMapper.toDto(role)).thenReturn(roleDto);

	RoleDTO result = roleService.createRole(roleCreateDto);

	assertThat(result).isNotNull();
	assertThat(result.getName()).isEqualTo(ROLE_NAME);
	verify(roleRepository).save(any(Role.class));
  }

  @Test
  void updateRole_WhenExists_ShouldReturnUpdatedDto() {
	// Настраиваем мок так, чтобы он возвращал роль, которую мы "сохранили"
	when(roleRepository.findById(ID)).thenReturn(Optional.of(role));
	when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

	RoleDTO updatedDto = RoleDTO.builder().id(ID).name(UPDATED_NAME).build();
	when(roleMapper.toDto(any(Role.class))).thenReturn(updatedDto);

	RoleDTO result = roleService.updateRole(ID, roleUpdateDto);

	assertThat(result).isNotNull();
	assertThat(result.getName()).isEqualTo(UPDATED_NAME);
	verify(roleRepository).save(any(Role.class));
	assertThat(role.getName()).isEqualTo(UPDATED_NAME);
  }

  @Test
  void updateRole_WhenNotExists_ShouldReturnNull() {
	when(roleRepository.findById(ID)).thenReturn(Optional.empty());

	RoleDTO result = roleService.updateRole(ID, roleUpdateDto);

	assertThat(result).isNull();
	verify(roleRepository, never()).save(any());
  }

  @Test
  void deleteRole_WhenExists_ShouldReturnTrue() {
	when(roleRepository.existsById(ID)).thenReturn(true);
	doNothing().when(roleRepository).deleteById(ID);

	boolean result = roleService.deleteRole(ID);

	assertThat(result).isTrue();
	verify(roleRepository).deleteById(ID);
  }

  @Test
  void deleteRole_WhenNotExists_ShouldReturnFalse() {
	when(roleRepository.existsById(ID)).thenReturn(false);

	boolean result = roleService.deleteRole(ID);

	assertThat(result).isFalse();
	verify(roleRepository, never()).deleteById(ID);
  }
}