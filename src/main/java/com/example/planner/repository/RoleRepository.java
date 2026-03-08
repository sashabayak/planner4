// src/main/java/com/example/planner/repository/RoleRepository.java
package com.example.planner.repository;

import com.example.planner.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}