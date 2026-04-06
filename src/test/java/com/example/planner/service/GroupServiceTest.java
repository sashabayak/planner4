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
  void getGroupByIdWhenNotExistsShouldReturnNull() {
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
		.Name(UPDATED_FIRST_NAME)
		.lastName(UPDATED_LAST_NAME)
		.phone(UPDATED_PHONE)
		.email(UPDATED_EMAIL)
		.build();

	ClientDto updatedDto = ClientDto.builder()
		.id(ID)
		.firstName(UPDATED_FIRST_NAME)
		.lastName(UPDATED_LAST_NAME)
		.phone(UPDATED_PHONE)
		.email(UPDATED_EMAIL)
		.build();

	when(clientRepository.findById(ID)).thenReturn(Optional.of(client));
	when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);
	when(clientMapper.toDto(updatedClient)).thenReturn(updatedDto);

	ClientDto result = clientService.updateClient(ID, clientUpdateDto);

	assertThat(result).isNotNull();
	assertThat(result.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
	assertThat(result.getLastName()).isEqualTo(UPDATED_LAST_NAME);
	assertThat(result.getPhone()).isEqualTo(UPDATED_PHONE);
	assertThat(result.getEmail()).isEqualTo(UPDATED_EMAIL);
	verify(clientRepository, times(1)).save(any(Client.class));
  }

  @Test
  void updateClientWithPartialDataShouldUpdateOnlyProvidedFields() {
	Client updatedClient = Client.builder()
		.id(ID)
		.firstName(FIRST_NAME)
		.lastName(UPDATED_LAST_NAME)
		.phone(UPDATED_PHONE)
		.email(EMAIL)
		.build();

	ClientDto updatedDto = ClientDto.builder()
		.id(ID)
		.firstName(FIRST_NAME)
		.lastName(UPDATED_LAST_NAME)
		.phone(UPDATED_PHONE)
		.email(EMAIL)
		.build();

	when(clientRepository.findById(ID)).thenReturn(Optional.of(client));
	when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);
	when(clientMapper.toDto(updatedClient)).thenReturn(updatedDto);

	ClientDto result = clientService.updateClient(ID, partialUpdateDto);

	assertThat(result).isNotNull();
	assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
	assertThat(result.getLastName()).isEqualTo(UPDATED_LAST_NAME);
	assertThat(result.getPhone()).isEqualTo(UPDATED_PHONE);
	assertThat(result.getEmail()).isEqualTo(EMAIL);
	verify(clientRepository, times(1)).save(any(Client.class));
  }

  @Test
  void updateClientWhenClientNotExistsShouldReturnNull() {
	when(clientRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

	ClientDto result = clientService.updateClient(NON_EXISTENT_ID, clientUpdateDto);

	assertThat(result).isNull();
	verify(clientRepository, never()).save(any(Client.class));
  }

  @Test
  void deleteClientWhenClientExistsShouldReturnTrue() {
	when(clientRepository.existsById(ID)).thenReturn(true);
	doNothing().when(clientRepository).deleteById(ID);

	boolean result = clientService.deleteClient(ID);

	assertThat(result).isTrue();
	verify(clientRepository, times(1)).existsById(ID);
	verify(clientRepository, times(1)).deleteById(ID);
  }

  @Test
  void deleteClientWhenClientNotExistsShouldReturnFalse() {
	when(clientRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

	boolean result = clientService.deleteClient(NON_EXISTENT_ID);

	assertThat(result).isFalse();
	verify(clientRepository, times(1)).existsById(NON_EXISTENT_ID);
	verify(clientRepository, never()).deleteById(anyLong());
  }

  @Test
  void createClientsBulkShouldReturnListOfCreatedClients() {
	List<ClientCreateDto> createDtos = Arrays.asList(clientCreateDto, clientCreateDto);
	List<Client> clients = Arrays.asList(client, client);

	when(clientRepository.saveAll(anyList())).thenReturn(clients);
	when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

	List<ClientDto> result = clientService.createClientsBulk(createDtos);

	assertThat(result).hasSize(2);
	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createClientsBulkWithEmptyListShouldReturnEmptyList() {
	List<ClientCreateDto> createDtos = List.of();

	when(clientRepository.saveAll(anyList())).thenReturn(List.of());

	List<ClientDto> result = clientService.createClientsBulk(createDtos);

	assertThat(result).isEmpty();
	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createClientsBulkWithTransactionWhenLessOrEqual3ShouldSucceed() {
	List<ClientCreateDto> createDtos = Arrays.asList(
		clientCreateDto, clientCreateDto, clientCreateDto
	);
	List<Client> clients = Arrays.asList(client, client, client);

	when(clientRepository.saveAll(anyList())).thenReturn(clients);
	when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

	List<ClientDto> result = clientService.createClientsBulkWithTransaction(createDtos);

	assertThat(result).hasSize(3);
	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createClientsBulkWithTransactionWhenMoreThan3ShouldThrowException() {
	List<ClientCreateDto> createDtos = Arrays.asList(
		clientCreateDto, clientCreateDto, clientCreateDto, clientCreateDto
	);
	List<Client> clients = Arrays.asList(client, client, client, client);

	when(clientRepository.saveAll(anyList())).thenReturn(clients);

	assertThatThrownBy(() -> clientService.createClientsBulkWithTransaction(createDtos))
		.isInstanceOf(NoSuchElementException.class)
		.hasMessageContaining("Превышен лимит")
		.hasMessageContaining("Транзакция будет откачена");

	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createClientsBulkWithoutTransactionWhenLessOrEqual3ShouldSucceed() {
	List<ClientCreateDto> createDtos = Arrays.asList(
		clientCreateDto, clientCreateDto, clientCreateDto
	);
	List<Client> clients = Arrays.asList(client, client, client);

	when(clientRepository.saveAll(anyList())).thenReturn(clients);
	when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

	List<ClientDto> result = clientService.createClientsBulkWithoutTransaction(createDtos);

	assertThat(result).hasSize(3);
	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void createClientsBulkWithoutTransactionWhenMoreThan3ShouldThrowExceptionButDataSaved() {
	List<ClientCreateDto> createDtos = Arrays.asList(
		clientCreateDto, clientCreateDto, clientCreateDto, clientCreateDto
	);
	List<Client> clients = Arrays.asList(client, client, client, client);

	when(clientRepository.saveAll(anyList())).thenReturn(clients);

	assertThatThrownBy(() -> clientService.createClientsBulkWithoutTransaction(createDtos))
		.isInstanceOf(NoSuchElementException.class)
		.hasMessageContaining("Превышен лимит")
		.hasMessageContaining("Данные уже сохранены");

	verify(clientRepository, times(1)).saveAll(anyList());
  }

  @Test
  void getClientByEmailWhenExistsShouldReturnClient() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);
	when(clientMapper.toDto(client)).thenReturn(clientDto);

	ClientDto result = clientService.getClientByEmail(EMAIL);

	assertThat(result).isNotNull();
	assertThat(result.getEmail()).isEqualTo(EMAIL);
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientByEmailWhenNotExistsShouldReturnNull() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);

	ClientDto result = clientService.getClientByEmail("notexists@mail.com");

	assertThat(result).isNull();
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientByEmailWhenEmailIsNullShouldReturnNull() {
	Client clientWithNullEmail = Client.builder()
		.id(ID)
		.firstName(FIRST_NAME)
		.lastName(LAST_NAME)
		.phone(PHONE)
		.email(null)
		.build();

	when(clientRepository.findAll()).thenReturn(List.of(clientWithNullEmail));

	ClientDto result = clientService.getClientByEmail(EMAIL);

	assertThat(result).isNull();
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientByEmailWhenMultipleClientsShouldReturnFirstMatch() {
	Client client1 = Client.builder()
		.id(1L)
		.firstName(FIRST_NAME)
		.lastName(LAST_NAME)
		.phone(PHONE)
		.email(EMAIL)
		.build();

	Client client2 = Client.builder()
		.id(2L)
		.firstName("Петр")
		.lastName("Сидоров")
		.phone(PHONE)
		.email(EMAIL)
		.build();

	ClientDto clientDto1 = ClientDto.builder()
		.id(1L)
		.firstName(FIRST_NAME)
		.lastName(LAST_NAME)
		.phone(PHONE)
		.email(EMAIL)
		.build();

	List<Client> clients = Arrays.asList(client1, client2);
	when(clientRepository.findAll()).thenReturn(clients);
	when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto1);

	ClientDto result = clientService.getClientByEmail(EMAIL);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(1L);
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientByEmailWithNullParameterShouldReturnNull() {
	ClientDto result = clientService.getClientByEmail(null);
	assertThat(result).isNull();
	verify(clientRepository, never()).findAll();
  }

  @Test
  void getClientByEmailWhenEmailIsEmptyStringShouldReturnNull() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);

	ClientDto result = clientService.getClientByEmail("");

	assertThat(result).isNull();
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientsByPhonePatternShouldReturnFilteredList() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);
	when(clientMapper.toDto(client)).thenReturn(clientDto);

	List<ClientDto> result = clientService.getClientsByPhonePattern("123");

	assertThat(result).hasSize(1);
	assertThat(result.get(0).getPhone()).contains("123");
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientsByPhonePatternWhenNoMatchShouldReturnEmptyList() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);

	List<ClientDto> result = clientService.getClientsByPhonePattern("999");

	assertThat(result).isEmpty();
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientsByPhonePatternWhenPhoneIsNullShouldSkip() {
	Client clientWithNullPhone = Client.builder()
		.id(ID)
		.firstName(FIRST_NAME)
		.lastName(LAST_NAME)
		.phone(null)
		.email(EMAIL)
		.build();

	when(clientRepository.findAll()).thenReturn(List.of(clientWithNullPhone));

	List<ClientDto> result = clientService.getClientsByPhonePattern("123");

	assertThat(result).isEmpty();
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientsByPhonePatternShouldReturnMultipleMatches() {
	Client client2 = Client.builder()
		.id(2L)
		.firstName("Петр")
		.lastName("Сидоров")
		.phone("+375441234567")
		.email("petr@mail.com")
		.build();

	ClientDto clientDto2 = ClientDto.builder()
		.id(2L)
		.firstName("Петр")
		.lastName("Сидоров")
		.phone("+375441234567")
		.email("petr@mail.com")
		.build();

	List<Client> clients = Arrays.asList(client, client2);
	when(clientRepository.findAll()).thenReturn(clients);
	when(clientMapper.toDto(client)).thenReturn(clientDto);
	when(clientMapper.toDto(client2)).thenReturn(clientDto2);

	List<ClientDto> result = clientService.getClientsByPhonePattern("123");

	assertThat(result).hasSize(2);
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void getClientsByPhonePatternWithNullParameterShouldReturnEmptyList() {
	List<ClientDto> result = clientService.getClientsByPhonePattern(null);
	assertThat(result).isEmpty();
	verify(clientRepository, never()).findAll();
  }

  @Test
  void getClientsByPhonePatternWithEmptyPatternShouldReturnAllWithPhone() {
	List<Client> clients = List.of(client);
	when(clientRepository.findAll()).thenReturn(clients);
	when(clientMapper.toDto(client)).thenReturn(clientDto);

	List<ClientDto> result = clientService.getClientsByPhonePattern("");

	assertThat(result).hasSize(1);
	verify(clientRepository, times(1)).findAll();
  }

  @Test
  void updateClientWithAllFieldsNullShouldNotUpdateAnything() {
	ClientUpdateDto emptyUpdate = new ClientUpdateDto();

	when(clientRepository.findById(ID)).thenReturn(Optional.of(client));
	when(clientRepository.save(any(Client.class))).thenReturn(client);
	when(clientMapper.toDto(client)).thenReturn(clientDto);

	ClientDto result = clientService.updateClient(ID, emptyUpdate);

	assertThat(result).isNotNull();
	assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
	assertThat(result.getLastName()).isEqualTo(LAST_NAME);
	verify(clientRepository, times(1)).save(any(Client.class));
  }

  @Test
  void deleteClientWhenRepositoryThrowsExceptionShouldStillReturnTrue() {
	when(clientRepository.existsById(ID)).thenReturn(true);
	doNothing().when(clientRepository).deleteById(ID);

	boolean result = clientService.deleteClient(ID);

	assertThat(result).isTrue();
	verify(clientRepository, times(1)).deleteById(ID);
  }

  @Test
  void createClientWithNullFieldsShouldHandleGracefully() {
	ClientCreateDto dtoWithNulls = new ClientCreateDto();
	dtoWithNulls.setFirstName(null);
	dtoWithNulls.setLastName(null);
	dtoWithNulls.setPhone(null);
	dtoWithNulls.setEmail(null);

	Client clientWithNulls = Client.builder()
		.id(ID)
		.firstName(null)
		.lastName(null)
		.phone(null)
		.email(null)
		.build();

	ClientDto dtoWithNullsResult = ClientDto.builder()
		.id(ID)
		.firstName(null)
		.lastName(null)
		.phone(null)
		.email(null)
		.build();

	when(clientRepository.save(any(Client.class))).thenReturn(clientWithNulls);
	when(clientMapper.toDto(any(Client.class))).thenReturn(dtoWithNullsResult);

	ClientDto result = clientService.createClient(dtoWithNulls);

	assertThat(result).isNotNull();
	assertThat(result.getFirstName()).isNull();
	verify(clientRepository, times(1)).save(any(Client.class));
  }

  @Test
  void convertToEntityShouldMapAllFields() {
	ClientCreateDto dto = new ClientCreateDto();
	dto.setFirstName(FIRST_NAME);
	dto.setLastName(LAST_NAME);
	dto.setPhone(PHONE);
	dto.setEmail(EMAIL);

	when(clientRepository.save(any(Client.class))).thenReturn(client);
	when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

	ClientDto result = clientService.createClient(dto);

	assertThat(result).isNotNull();
	assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
	assertThat(result.getLastName()).isEqualTo(LAST_NAME);
	assertThat(result.getPhone()).isEqualTo(PHONE);
	assertThat(result.getEmail()).isEqualTo(EMAIL);
  }
}