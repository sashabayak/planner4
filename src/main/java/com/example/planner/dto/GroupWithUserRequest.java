package com.example.planner.dto;

import lombok.Data;

@Data
public class GroupWithUserRequest {
  private GroupDTO group;
  private UserDTO user;
}