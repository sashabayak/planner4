package com.example.planner.dto.group;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GroupBulkCreateDTO {
  @NotEmpty(message = "Список групп не может быть пустым")
  @Size(max = 10, message = "Нельзя создать более 10 групп за раз")
  @Valid
  private List<GroupCreateDTO> groups;
}