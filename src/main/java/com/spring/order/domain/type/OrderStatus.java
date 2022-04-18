package com.spring.order.domain.type;

public enum OrderStatus {
	ACCEPTED,
	PAYMENT_REQUIRED,
	PAYMENT_CONFIRM,
	PAYMENT_REJECTED,
	READY_FOR_DELIVERY,
	SHIPPED,
	SETTLED,
	CANCELED;

}
