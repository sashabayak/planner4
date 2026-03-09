package com.example.planner.repository;

import com.example.planner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.items")
  List<User> findAllWithItems();
}