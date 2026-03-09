package com.example.planner.dto;

import java.util.List;
import lombok.Data;
import java.time.Instant;

@Data
public class UserDTO {
  private Integer id;
  private String name;
  private Instant birthDate;
  private Integer roleId;
  private Integer groupId;
  private List<ItemDTO> items;
}