package com.example.planner.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ItemDTO {
  private Integer id;
  private String name;
  private String description;
  private boolean completed;
  private Instant createdAt;
  private List<TagDTO> tags;
}