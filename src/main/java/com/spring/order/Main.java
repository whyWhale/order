package com.spring.order;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import com.spring.order.context.ApplicationConfiguration;
import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.domain.Voucher.PercentDiscountVoucher;
import com.spring.order.service.OrderService;

public class Main {
	public static void main(String[] args) {
		UUID customerId = UUID.randomUUID();
		// what : spring container 등록
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
				ApplicationConfiguration.class);
		OrderService orderService = applicationContext.getBean(OrderService.class);
		ArrayList<OrderItem> orderItems = new ArrayList<>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}};

		orderService.createOrder(customerId, orderItems);
		var order = new Order(UUID.randomUUID(), customerId, orderItems,
				new PercentDiscountVoucher(UUID.randomUUID(), 10));

		Assert.isTrue(order.getTotalAmount() == 100L,
				MessageFormat.format("total amount {0} is not 90L", order.getTotalAmount()));

	}
}
