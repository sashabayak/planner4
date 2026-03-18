package com.example.planner.dto.user;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public record UserFilterDTO(
	String roleName,
	String groupName,
	@Min(value = 0, message = "Номер страницы должен быть неотрицательным") Integer page,
	@Min(value = 1, message = "Размер страницы должен быть не менее 1") Integer size,
	String sortBy,
	String sortDirection
) {
  public UserFilterDTO {
	page = page != null ? page : 0;
	size = size != null ? size : 10;
	sortBy = sortBy != null ? sortBy : "name";
	sortDirection = sortDirection != null ? sortDirection : "ASC";
  }

  public Pageable toPageable() {
	Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
	return PageRequest.of(page, size, sort);
  }

  public static Builder builder() {
	return new Builder();
  }

  public static class Builder {
	private String roleName;
	private String groupName;
	private Integer page = 0;
	private Integer size = 10;
	private String sortBy = "name";
	private String sortDirection = "ASC";



	public Builder roleName(String roleName) {
	  this.roleName = roleName;
	  return this;
	}

	public Builder groupName(String groupName) {
	  this.groupName = groupName;
	  return this;
	}

	public Builder page(Integer page) {
	  this.page = page != null ? page : 0;
	  return this;
	}

	public Builder size(Integer size) {
	  this.size = size != null ? size : 10;
	  return this;
	}

	public Builder sortBy(String sortBy) {
	  this.sortBy = sortBy != null ? sortBy : "name";
	  return this;
	}

	public Builder sortDirection(String sortDirection) {
	  this.sortDirection = sortDirection != null ? sortDirection : "ASC";
	  return this;
	}

	public UserFilterDTO build() {
	  return new UserFilterDTO(
		 roleName,
		  groupName, page, size, sortBy, sortDirection
	  );
	}
  }
}