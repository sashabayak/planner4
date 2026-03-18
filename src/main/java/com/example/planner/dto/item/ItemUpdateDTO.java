package com.example.planner.dto.item;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDTO {
  @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
  private String name;

  @Size(min = 2, max = 200, message = "Описание должно содержать от 2 до 200 символов")
  private String description;

  private Boolean completed;
}