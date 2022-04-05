package com.spring.order;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.util.Assert;

import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.domain.Voucher.PercentDiscountVoucher;

public class Main {
	public static void main(String[] args) {
		UUID customerId = UUID.randomUUID();
		ArrayList<OrderItem> orderItems = new ArrayList<>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}};
		// 10을 빼는 건지 ? 10퍼센트가 할인되는 건지 어떻게 알지?
		var order = new Order(UUID.randomUUID(), customerId, orderItems,
				new PercentDiscountVoucher(UUID.randomUUID(), 10));

		Assert.isTrue(order.getTotalAmount() == 90L,
				MessageFormat.format("total amount {0} is not 90L", order.getTotalAmount()));

	}
}
