package com.example.planner.cache;

public record UserQueryKey(
	String groupName,
	String roleName,
	Integer page,
	Integer size
) {
}