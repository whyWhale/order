package com.spring.order.repository;

import com.spring.order.domain.Order;

public interface OrderRepository {
	void insert(Order order);
}
