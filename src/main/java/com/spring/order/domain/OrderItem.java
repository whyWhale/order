package com.spring.order.domain;

import java.util.UUID;

public class OrderItem {
	private final UUID productId;
	private final long productPrice;
	private final long quantity;

	public OrderItem(UUID productId, long productPrice, int quantity) {
		this.productId = productId;
		this.productPrice = productPrice;
		this.quantity = quantity;
	}

	public long getProductPrice() {
		return productPrice;
	}

	public long getQuantity() {
		return quantity;
	}
}
