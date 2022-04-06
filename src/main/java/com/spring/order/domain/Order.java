package com.spring.order.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.spring.order.domain.enumtype.OrderStatus;
import com.spring.order.domain.Voucher.Voucher;

public class Order {
	private final UUID orderId;
	private final UUID customerId;
	private Optional<Voucher> voucher;
	private OrderStatus orderStatus = OrderStatus.ACCEPTED;

	private final List<OrderItem> orderItemList;

	public Order(UUID orderId, UUID customerId, List<OrderItem> orderItems, Voucher voucher) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.orderItemList = orderItems;
		this.voucher = Optional.of(voucher);
	}

	public Order(UUID orderId, UUID customerId, List<OrderItem> orderItems) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.orderItemList = orderItems;
		this.voucher = Optional.empty();
	}

	public long getTotalAmount() {
		Long beforeDiscount = orderItemList.stream()
				.map(orderItem -> orderItem.getProductPrice() * orderItem.getQuantity())
				.reduce(0L, Long::sum);
		return voucher.map(value -> value.discount(beforeDiscount)).orElse(beforeDiscount);
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public UUID getOrderId() {
		return orderId;
	}
}
