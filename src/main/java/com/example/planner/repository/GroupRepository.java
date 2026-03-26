package com.example.planner.repository;

import com.example.planner.entity.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}