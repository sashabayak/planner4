// src/main/java/com/example/planner/repository/GroupRepository.java
package com.example.planner.repository;

import com.example.planner.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}