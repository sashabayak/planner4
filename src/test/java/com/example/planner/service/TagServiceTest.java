package com.example.planner.service;

import com.example.planner.dto.tag.TagDTO;
import com.example.planner.entity.Item;
import com.example.planner.entity.Tag;
import com.example.planner.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  private static final Integer ID = 1;
  private static final String TAG_NAME = "Работа";
  private static final String UPDATED_NAME = "Учеба";

  @Mock
  private TagRepository tagRepository;

  @InjectMocks
  private TagService tagService;

  private Tag tag;
  private TagDTO tagDto;

  @BeforeEach
  void setUp() {
	tag = new Tag();
	tag.setId(ID);
	tag.setName(TAG_NAME);

	tagDto = new TagDTO();
	tagDto.setId(ID);
	tagDto.setName(TAG_NAME);
  }

  @Test
  void findAll_ShouldReturnListOfDtos() {
	when(tagRepository.findAll()).thenReturn(List.of(tag));

	List<TagDTO> result = tagService.findAll();

	assertThat(result).hasSize(1);
	assertThat(result.get(0).getName()).isEqualTo(TAG_NAME);
	verify(tagRepository).findAll();
  }

  @Test
  void findById_WhenExists_ShouldReturnOptionalDto() {
	when(tagRepository.findById(ID)).thenReturn(Optional.of(tag));

	Optional<TagDTO> result = tagService.findById(ID);

	assertThat(result).isPresent();
	assertThat(result.get().getId()).isEqualTo(ID);
  }

  @Test
  void findById_WhenNotExists_ShouldReturnEmpty() {
	when(tagRepository.findById(ID)).thenReturn(Optional.empty());

	Optional<TagDTO> result = tagService.findById(ID);

	assertThat(result).isEmpty();
  }

  @Test
  void save_ShouldReturnDto() {
	when(tagRepository.save(any(Tag.class))).thenReturn(tag);

	TagDTO result = tagService.save(tagDto);

	assertThat(result).isNotNull();
	assertThat(result.getName()).isEqualTo(TAG_NAME);
	verify(tagRepository).save(any(Tag.class));
  }

  @Test
  void update_WhenExists_ShouldReturnUpdatedDto() {
	when(tagRepository.findById(ID)).thenReturn(Optional.of(tag));
	when(tagRepository.save(any(Tag.class))).thenReturn(tag);

	TagDTO updateInfo = new TagDTO();
	updateInfo.setName(UPDATED_NAME);

	Optional<TagDTO> result = tagService.update(ID, updateInfo);

	assertThat(result).isPresent();
	assertThat(tag.getName()).isEqualTo(UPDATED_NAME);
	verify(tagRepository).save(tag);
  }

  @Test
  void update_WhenNotExists_ShouldReturnEmpty() {
	when(tagRepository.findById(ID)).thenReturn(Optional.empty());

	Optional<TagDTO> result = tagService.update(ID, tagDto);

	assertThat(result).isEmpty();
	verify(tagRepository, never()).save(any());
  }

  @Test
  void deleteById_WhenExists_ShouldClearRelationsAndDelete() {
	// Создаем тестовый айтем со связью
	Item item = new Item();
	List<Tag> itemTags = new ArrayList<>();
	itemTags.add(tag);
	item.setTags(itemTags);

	tag.setItems(new ArrayList<>(List.of(item)));

	when(tagRepository.findById(ID)).thenReturn(Optional.of(tag));

	tagService.deleteById(ID);

	// Проверяем разрыв связей
	assertThat(item.getTags()).doesNotContain(tag);
	assertThat(tag.getItems()).isEmpty();

	verify(tagRepository).save(tag);
	verify(tagRepository).deleteById(ID);
  }

  @Test
  void deleteById_WhenNotExists_ShouldThrowException() {
	when(tagRepository.findById(ID)).thenReturn(Optional.empty());

	assertThatThrownBy(() -> tagService.deleteById(ID))
		.isInstanceOf(RuntimeException.class)
		.hasMessageContaining("Tag not found with id: " + ID);

	verify(tagRepository, never()).deleteById(any());
  }
}