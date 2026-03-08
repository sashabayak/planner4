// src/main/java/com/example/planner/dto/UserDTO.java
package com.example.planner.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDTO {
  private Integer id;
  private String name;
  private Instant birthDate;
  private Integer roleId;
  private Integer groupId;
}