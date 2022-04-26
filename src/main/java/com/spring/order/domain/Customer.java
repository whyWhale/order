package com.spring.order.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Customer {
	private final UUID id;
	private String name;
	private final String email;
	private LocalDateTime lastLoginAt;
	private final LocalDateTime createdAt;

	public Customer(UUID id, String name, String email, LocalDateTime createdAt) {
		this.validateName(name);
		this.id = id;
		this.name = name;
		this.email = email;
		this.createdAt = createdAt;
	}

	public Customer(UUID id, String name, String email, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.lastLoginAt = lastLoginAt;
		this.createdAt = createdAt;
	}

	public void changeName(String name) {
		validateName(name);
		this.name = name;
	}

	public void changeLoginAt(LocalDateTime localDateTime) {
		this.lastLoginAt = localDateTime;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	private void validateName(String name) {
		if (name.isBlank()) {
			throw new RuntimeException("name should not empty!");
		}
	}

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", lastLoginAt=" + lastLoginAt +
				", createdAt=" + createdAt +
				'}';
	}
}
