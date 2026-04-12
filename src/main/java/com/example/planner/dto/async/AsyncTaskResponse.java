package com.example.planner.dto.async;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskResponse {
  private String taskId;
  private String status;
  private String message;
  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
  private Long resourceId;
  private String resourceName;
  private Object result;
  private String error;
}