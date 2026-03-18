package com.example.planner.dto;

import lombok.Data;
import com.example.planner.dto.group.GroupDTO;
import com.example.planner.dto.user.UserDTO;
@Data
public class GroupWithUserRequest {
  private GroupDTO group;
  private UserDTO user;
}