package com.example.planner.dto.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private long id;
  private String name;
  private LocalDate birthDate;
  private long roleId;
  private String roleName;
  private long groupId;
  private String groupName;
}