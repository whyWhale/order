package com.spring.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.order.domain.Customer;
import com.spring.order.repository.CustomerNamedJdbcRepository;
import com.spring.order.repository.CustomerRepository;

@Transactional(readOnly = true)
@Service
public class CustomerService {

	private final CustomerRepository customerNamedJdbcRepository;

	public CustomerService(CustomerNamedJdbcRepository customerNamedJdbcRepository) {
		this.customerNamedJdbcRepository = customerNamedJdbcRepository;
	}

	@Transactional
	public List<Customer> createCustomers(List<Customer> customers) {
		return customers.stream()
				.map(customerNamedJdbcRepository::insert)
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteAll() {
		customerNamedJdbcRepository.deleteAll();
	}

	@Transactional
	public List<Customer> findAll() {
		return customerNamedJdbcRepository.findAll();
	}
}
