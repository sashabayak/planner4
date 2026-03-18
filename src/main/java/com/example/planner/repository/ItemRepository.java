package com.example.planner.repository;

import com.example.planner.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
//package com.example.planner.repository;
//
//import com.example.planner.entity.Item;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface ItemRepository extends JpaRepository<Item, Integer> {
//  @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//  List<Item> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
//  @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.tags")
//  List<Item> findAllWithTags();
//
//  @Query("SELECT DISTINCT i FROM Item i JOIN i.tags t WHERE t.id = :tagId")
//  List<Item> findItemsByTagId(@Param("tagId") Integer tagId);
//}