package com.example.planner.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
  @NotBlank(message = "Имя клиента обязательно")
  private String name;

  @NotNull(message = "Дата рождения обязательна")
  @Past(message = "Дата должна быть в прошлом")
  private LocalDate birthDate;

  @NotNull(message = "ID роли обязательно")
  private Long roleId;

  @NotNull(message = "ID группы обязательно")
  private Long groupId;

}