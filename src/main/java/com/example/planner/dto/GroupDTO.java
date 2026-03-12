package com.example.planner.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {
  private Integer id;
  private String name;
  private List<UserDTO> users;
}