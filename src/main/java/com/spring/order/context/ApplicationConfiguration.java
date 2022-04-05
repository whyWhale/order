package com.spring.order.context;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.order.domain.Order;
import com.spring.order.domain.Voucher.Voucher;
import com.spring.order.repository.OrderRepository;
import com.spring.order.service.OrderService;
import com.spring.order.service.VoucherRepository;
import com.spring.order.service.VoucherService;

/**
 * what : voucher service, order service, repository 생성 책임을 가짐
 * 	각각 서비스, 저장소의 wiring(의존관계 담당) 을 담당.
 * 	:= 인스턴스 객체 생성의 제어권을 가지고 있는 클래스이다.(IoC container)
 * 	- 객체들의 생성과 파괴를
 */

@Configuration
public class ApplicationConfiguration {

	@Bean
	public VoucherRepository voucherRepository() {
		return new VoucherRepository() {
			@Override
			public Optional<Voucher> findById(UUID voucherId) {
				return Optional.empty();
			}
		};
	}

	@Bean
	public VoucherService voucherService(VoucherRepository voucherRepository) {
		return new VoucherService(voucherRepository);
	}

	@Bean
	public OrderRepository orderRepository() {
		return new OrderRepository() {
			@Override
			public void insert(Order order) {

			}
		};
	}

	@Bean
	public OrderService orderService(VoucherService voucherService, OrderRepository orderRepository) {
		return new OrderService(voucherService, orderRepository);
	}
}
