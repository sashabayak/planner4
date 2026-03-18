package com.example.planner.repository;

import com.example.planner.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
//package com.example.planner.repository;
//
//import com.example.planner.entity.Role;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface RoleRepository extends JpaRepository<Role, Integer> {
//}