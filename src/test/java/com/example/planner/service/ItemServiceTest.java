package com.example.planner.service;

import com.example.planner.dto.item.ItemCreateDTO;
import com.example.planner.dto.item.ItemDTO;
import com.example.planner.dto.item.ItemUpdateDTO;
import com.example.planner.entity.Item;
import com.example.planner.mapper.ItemMapper;
import com.example.planner.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  private static final Long ID = 1L;
  private static final String NAME = "Купить хлеб";
  private static final String DESCRIPTION = "В магазине у дома";
  private static final String UPDATED_NAME = "Купить молоко";

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ItemMapper itemMapper;

  @InjectMocks
  private ItemService itemService;

  private Item item;
  private ItemDTO itemDto;
  private ItemCreateDTO itemCreateDto;
  private ItemUpdateDTO itemUpdateDto;

  @BeforeEach
  void setUp() {
	item = Item.builder()
		.id(ID)
		.name(NAME)
		.description(DESCRIPTION)
		.completed(false)
		.createdAt(LocalDateTime.now())
		.build();

	itemDto = ItemDTO.builder()
		.id(ID)
		.name(NAME)
		.description(DESCRIPTION)
		.completed(false)
		.build();

	itemCreateDto = ItemCreateDTO.builder()
		.name(NAME)
		.description(DESCRIPTION)
		.completed(false)
		.build();

	itemUpdateDto = ItemUpdateDTO.builder()
		.name(UPDATED_NAME)
		.description(DESCRIPTION)
		.completed(true)
		.build();
  }

  @Test
  void getAllItems_ShouldReturnListOfDtos() {
	when(itemRepository.findAll()).thenReturn(List.of(item));
	when(itemMapper.toDto(item)).thenReturn(itemDto);

	List<ItemDTO> result = itemService.getAllItems();

	assertThat(result).hasSize(1);
	assertThat(result.get(0).getName()).isEqualTo(NAME);
	verify(itemRepository).findAll();
  }

  @Test
  void getItemById_WhenExists_ShouldReturnDto() {
	when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
	when(itemMapper.toDto(item)).thenReturn(itemDto);

	ItemDTO result = itemService.getItemById(ID);

	assertThat(result).isNotNull();
	assertThat(result.getId()).isEqualTo(ID);
	verify(itemRepository).findById(ID);
  }

  @Test
  void getItemById_WhenNotExists_ShouldReturnNull() {
	when(itemRepository.findById(ID)).thenReturn(Optional.empty());

	ItemDTO result = itemService.getItemById(ID);

	assertThat(result).isNull();
	verify(itemRepository).findById(ID);
  }

  @Test
  void createItem_ShouldReturnDto() {
	when(itemMapper.toEntity(any(ItemCreateDTO.class))).thenReturn(item);
	when(itemRepository.save(any(Item.class))).thenReturn(item);
	when(itemMapper.toDto(item)).thenReturn(itemDto);

	ItemDTO result = itemService.createItem(itemCreateDto);

	assertThat(result).isNotNull();
	assertThat(result.getName()).isEqualTo(NAME);
	verify(itemRepository).save(any(Item.class));
  }

  @Test
  void updateItem_WhenExists_ShouldReturnUpdatedDto() {
	when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
	when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);
	when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

	ItemDTO result = itemService.updateItem(ID, itemUpdateDto);

	assertThat(result).isNotNull();
	verify(itemRepository).save(any(Item.class));
	assertThat(item.getName()).isEqualTo(UPDATED_NAME);
	assertThat(item.isCompleted()).isTrue();
  }

  @Test
  void updateItem_WhenExistsAndCompletedIsNull_ShouldNotChangeCompletedStatus() {
	itemUpdateDto.setCompleted(null);
	item.setCompleted(false);

	when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
	when(itemRepository.save(any(Item.class))).thenReturn(item);
	when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

	itemService.updateItem(ID, itemUpdateDto);

	assertThat(item.isCompleted()).isFalse(); // Статус не должен измениться на null
  }

  @Test
  void updateItem_WhenNotExists_ShouldReturnNull() {
	when(itemRepository.findById(ID)).thenReturn(Optional.empty());

	ItemDTO result = itemService.updateItem(ID, itemUpdateDto);

	assertThat(result).isNull();
	verify(itemRepository, never()).save(any());
  }

  @Test
  void deleteItem_WhenExists_ShouldReturnTrue() {
	when(itemRepository.existsById(ID)).thenReturn(true);
	doNothing().when(itemRepository).deleteById(ID);

	boolean result = itemService.deleteItem(ID);

	assertThat(result).isTrue();
	verify(itemRepository).deleteById(ID);
  }

  @Test
  void deleteItem_WhenNotExists_ShouldReturnFalse() {
	when(itemRepository.existsById(ID)).thenReturn(false);

	boolean result = itemService.deleteItem(ID);

	assertThat(result).isFalse();
	verify(itemRepository, never()).deleteById(ID);
  }
}