package com.example.planner.repository;

import com.example.planner.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
  @Query("SELECT DISTINCT g FROM Group g LEFT JOIN FETCH g.users")
  List<Group> findAllWithUsers();

  @Query("SELECT DISTINCT g FROM Group g LEFT JOIN FETCH g.users WHERE g.id = :id")
  Optional<Group> findByIdWithUsers(@Param("id") Integer id);

}