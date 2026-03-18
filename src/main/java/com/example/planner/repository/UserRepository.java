package com.example.planner.repository;

import com.example.planner.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findByGroupId(Long groupId);

  @Query("SELECT u FROM User u")
  List<User> findAllWithoutFetch();

  @EntityGraph(attributePaths = {"group", "role"})
  @Query("SELECT u FROM User u")
  List<User> findAllWithEntityGraph();

  @Query("SELECT DISTINCT u FROM User u " +
      "LEFT JOIN FETCH u.group g " +
      "LEFT JOIN FETCH u.role r " +
      "WHERE (" +
      "    LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%')) " +
      "    OR :groupName IS NULL" +
      ") " +
      "AND (" +
      "    LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')) " +
      "    OR :roleName IS NULL" +
      ")")
  List<User> findUsersWithFiltersJpql(
      @Param("groupName") String groupName,
      @Param("roleName") String roleName);

  @Query(value = "SELECT DISTINCT u.* FROM users u " +
      "LEFT JOIN groups g ON u.group_id = g.id " +
      "LEFT JOIN roles r ON u.role_id = r.id " +
      "WHERE (" +
      "    LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%')) " +
      "    OR :groupName IS NULL" +
      ") " +
      "AND (" +
      "    LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')) " +
      "    OR :roleName IS NULL" +
      ")",
      nativeQuery = true)
  List<User> findUsersWithFiltersNative(
      @Param("groupName") String groupName,
      @Param("roleName") String roleName);

  @Query(value = "SELECT DISTINCT u.* FROM users u " +
      "LEFT JOIN groups g ON u.group_id = g.id " +
      "LEFT JOIN roles r ON u.role_id = r.id " +
      "WHERE (" +
      "    LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%')) " +
      "    OR :groupName IS NULL" +
      ") " +
      "AND (" +
      "    LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')) " +
      "    OR :roleName IS NULL" +
      ")",
      countQuery = "SELECT COUNT(DISTINCT u.id) FROM users u " +
          "LEFT JOIN groups g ON u.group_id = g.id " +
          "LEFT JOIN roles r ON u.role_id = r.id " +
          "WHERE (" +
          "    LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%')) " +
          "    OR :groupName IS NULL" +
          ") " +
          "AND (" +
          "    LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')) " +
          "    OR :roleName IS NULL" +
          ")",
      nativeQuery = true)
  Page<User> findUsersWithFiltersPaged(
      @Param("groupName") String groupName,
      @Param("roleName") String roleName,
      Pageable pageable);
}
