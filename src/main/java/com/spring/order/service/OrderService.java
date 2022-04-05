package com.spring.order.service;

import java.util.List;
import java.util.UUID;

import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.repository.OrderRepository;

public class OrderService {
	private final VoucherService voucherService;
	private final OrderRepository orderRepository;

	public OrderService(VoucherService voucherService, OrderRepository orderRepository) {
		this.voucherService = voucherService;
		this.orderRepository = orderRepository;
	}

	public Order createOrder(UUID customerId, List<OrderItem> orderItmes, UUID voucherId) {
		var voucher = voucherService.getVoucher(voucherId);
		Order order = new Order(UUID.randomUUID(), customerId, orderItmes, voucher);
		orderRepository.insert(order);
		voucherService.useVoucher(voucher);
		return order;
	}

	/**
	 * what : voucher 없이 만들기
	 * @param customerId
	 * @param orderItmes
	 * @return
	 */
	public Order createOrder(UUID customerId, List<OrderItem> orderItems) {
		Order order = new Order(UUID.randomUUID(), customerId, orderItems);
		orderRepository.insert(order);
		return order;
	}
}
