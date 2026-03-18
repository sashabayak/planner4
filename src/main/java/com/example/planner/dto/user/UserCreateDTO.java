package com.example.planner.dto.user;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
  @NotNull(message = "Имя обязательна")
  private String name;

  @NotNull(message = "Дата рождения обязательна")
  private LocalDate birthDate;

  @NotNull(message = "ID пользователя обязателен")
  private Long roleId;

  @NotNull(message = "ID группы обязателен")
  private Long groupId;

}