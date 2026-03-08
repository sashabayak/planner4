// src/main/java/com/example/planner/repository/ItemRepository.java
package com.example.planner.repository;

import com.example.planner.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
  @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Item> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
}