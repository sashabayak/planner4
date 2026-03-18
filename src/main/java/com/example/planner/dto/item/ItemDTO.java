package com.example.planner.dto.item;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.example.planner.dto.tag.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
}