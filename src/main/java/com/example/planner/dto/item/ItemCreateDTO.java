package com.example.planner.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDTO {
  @NotBlank(message = "Имя задачи обязательно")
  private String name;

  @NotBlank(message = "Описание обязательно")
  private String description;

  private Boolean completed = false;

}