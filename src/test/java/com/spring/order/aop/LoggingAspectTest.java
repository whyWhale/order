package com.spring.order.aop;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.domain.Voucher.FixedAmountVoucher;
import com.spring.order.domain.type.OrderStatus;
import com.spring.order.repository.VoucherRepository;
import com.spring.order.service.OrderService;

@SpringJUnitConfig
class LoggingAspectTest {

	@Configuration
	@ComponentScan(
			basePackages = {"com.spring.order"}
	)

	@EnableAspectJAutoProxy // note : aop 적용
	static class Config {

	}

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	OrderService orderService;

	@Autowired
	VoucherRepository voucherRepository;

	@Test
	@DisplayName("application Context 생성")
	public void containerTest() {
		//given
		VoucherRepository voucherRepositoryBean = applicationContext.getBean(VoucherRepository.class);
		assertThat(voucherRepositoryBean, notNullValue());
		//when

		//then
		assertThat(applicationContext, notNullValue());
	}

	@Test
	@DisplayName("logging aspect test")
	public void OrderServiceTest() {
		//given
		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		voucherRepository.insert(fixedAmountVoucher);
	}

}