package com.spring.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;

import com.spring.order.domain.Customer;

@Profile("dev")
public interface CustomerRepository {

	int count();

	Customer insert(Customer customer);

	Customer update(Customer customer);

	List<Customer> findAll();

	Optional<Customer> findById(UUID customerId);

	Optional<Customer> findByName(String name);

	Optional<Customer> findByEmail(String email);

	void deleteAll();
}
