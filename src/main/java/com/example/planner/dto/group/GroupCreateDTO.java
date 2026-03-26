package com.example.planner.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupCreateDTO {
  @NotBlank(message = "Имя обязательно")
  @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
  private String name;
}