package com.spring.order.kdt;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.domain.Voucher.FixedAmountVoucher;
import com.spring.order.domain.type.OrderStatus;
import com.spring.order.repository.VoucherRepository;
import com.spring.order.service.OrderService;

// note : 하나의 애노테이션으로 대체 가능 : SpringJunitConfig
/**
 * @ExtendWith({SpringExtension.class})
 * @ContextConfiguration
 */

@ActiveProfiles("test")
@SpringJUnitConfig
public class SpringContextTesting {
	@Configuration
	@ComponentScan(
			basePackages = {"com.spring.order"}
	)
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
	@DisplayName("voucherRepository bean 생성 확인")
	public void createBeanTest() {
		//given
		VoucherRepository voucherRepositoryBean = applicationContext.getBean(VoucherRepository.class);
		//when
		//then
		assertThat(voucherRepositoryBean, notNullValue());
	}

	@Test
	@DisplayName("voucherService bean 생성 확인")
	public void createBeanTest2() {
		//given
		OrderService orderService = applicationContext.getBean(OrderService.class);
		//when
		//then
		assertThat(orderService, notNullValue());
	}

	@Test
	@DisplayName("orderService를 사용해서 주문을 생성할 수 있다.DS")
	public void OrderServiceTest() {
		//given
		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		voucherRepository.insert(fixedAmountVoucher);

		//when
		List<OrderItem> orderItems = List.of(new OrderItem(UUID.randomUUID(), 200, 1));

		Order order = orderService.createOrder(UUID.randomUUID(), orderItems, fixedAmountVoucher.getVoucherId());
		//then
		assertThat(order.getTotalAmount(), is(100L));
		assertThat(order.getVoucher().isEmpty(),is(false));
		assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
		assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));
	}
}

// // @ContextConfiguration(classes = ApplicationConfiguration.class)
