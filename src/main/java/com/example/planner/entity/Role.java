// src/main/java/com/example/planner/entity/Role.java
package com.example.planner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String name;

  @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
  private List<User> users = new ArrayList<>();

  public Role(String name) {
    this.name = name;
  }
}