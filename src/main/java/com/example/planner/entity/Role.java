package com.example.planner.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "roles")
@AllArgsConstructor
@Builder
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(length = 255)
  private String name;

  @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
  @Builder.Default
  private List<User> users = new ArrayList<>();
}