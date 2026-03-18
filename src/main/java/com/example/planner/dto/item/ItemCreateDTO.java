package com.example.planner.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDTO {
  @NotBlank(message = "Имя группы обязательно")
  @Size(min = 2, max = 50, message = "Имя группы должно содержать от 2 до 50 символов")
  private String name;

  @NotBlank(message = "Описание обязательно")
  @Size(min = 2, max = 50, message = "Описание должно содержать от 2 до 50 символов")
  private String description;

  private Boolean completed = false;

}