package com.spring.order.domain;

import java.util.List;
import java.util.UUID;

import com.spring.order.domain.enumtype.OrderStatus;
import com.spring.order.domain.Voucher.Voucher;

public class Order {
	private final UUID orderId;
	private final UUID customerId;
	private Voucher voucher;
	private OrderStatus orderStatus = OrderStatus.ACCEPTED;

	private final List<OrderItem> orderItemList;

	public Order(UUID orderId, UUID customerId, List<OrderItem> orderItemList, Voucher voucher) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.orderItemList = orderItemList;
		this.voucher = voucher;
	}

	public long getTotalAmount() {
		Long beforeDiscount = orderItemList.stream()
				.map(orderItem -> orderItem.getProductPrice() * orderItem.getQuantity())
				.reduce(0L, Long::sum);
		return voucher.discount(beforeDiscount);
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
}
