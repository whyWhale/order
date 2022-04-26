package com.spring.order.repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.spring.order.aop.TrackTime;
import com.spring.order.domain.Order;


@Repository
public class MemoryOrderRepository implements OrderRepository {
	private static final Map<UUID, Order> storage = new ConcurrentHashMap<>();

	@Override
	public Order insert(Order order) {
		return storage.put(order.getOrderId(), order);
	}
}
