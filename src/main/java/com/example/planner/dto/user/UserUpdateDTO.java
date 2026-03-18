package com.example.planner.dto.user;

import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {
  private String name;

  @Past(message = "Дата рождения должна быть в прошлом")
  private LocalDate birthDate;

  private Long roleId;

  private Long groupId;
}