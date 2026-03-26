package com.example.planner.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private LocalDate birthDate = LocalDate.now();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private Role role;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(name = "user_item",
	  joinColumns = @JoinColumn(name = "user_id"),
	  inverseJoinColumns = @JoinColumn(name = "item_id"))
  private List<Item> items = new ArrayList<>();

}