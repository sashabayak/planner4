package com.example.planner.dto.item; // укажите ваш точный пакет

import java.time.LocalDateTime;
import java.util.List;

import com.example.planner.dto.tag.TagDTO;
import com.example.planner.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
  private Long id;
  private String name;
  private String description;
  private boolean completed;
  private LocalDateTime createdAt;
  private List<TagDTO> tags;
  private List<UserDTO> users;
}