package com.example.planner.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDTO {
  private String name;

  private String description;

  private Boolean completed;
}