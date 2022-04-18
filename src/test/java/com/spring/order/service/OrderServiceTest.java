package com.spring.order.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import com.spring.order.domain.Order;
import com.spring.order.domain.OrderItem;
import com.spring.order.domain.Voucher.FixedAmountVoucher;
import com.spring.order.domain.type.OrderStatus;
import com.spring.order.repository.MemoryVoucherRepository;
import com.spring.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	class OrderRespositoryStub implements OrderRepository{

		@Override
		public Order insert(Order order) {
			return null;
		}
	}
	@DisplayName("order 생성되어야 한다 Stub version")
	@Test
	void createOrder() {
		// given
		MemoryVoucherRepository voucherRepository = new MemoryVoucherRepository();

		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		voucherRepository.insert(fixedAmountVoucher);

		VoucherService voucherService = new VoucherService(voucherRepository);
		OrderService orderService = new OrderService(voucherService, new OrderRespositoryStub());

		List<OrderItem> orderItems = List.of(new OrderItem(UUID.randomUUID(), 200, 1));
		// when
		Order order = orderService.createOrder(UUID.randomUUID(), orderItems, fixedAmountVoucher.getVoucherId());

		// then : order 상태에 집중
		assertThat(order.getTotalAmount(),is(100L));
		assertThat(order.getVoucher().isEmpty(),is(false));
		assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
		assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));

	}

	@Test
	@DisplayName("order가 생성되어야 한다. Mockito version")
	public void createOrderMockitoTest(){
	    //given
		VoucherService voucherServiceMock = mock(VoucherService.class);
		OrderRepository orderRepositoryMock = mock(OrderRepository.class);
		OrderService sut = new OrderService(voucherServiceMock, orderRepositoryMock);

		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		List<OrderItem> orderItems = List.of(new OrderItem(UUID.randomUUID(), 200, 1));

		when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher);

		//when
		Order order = sut.createOrder(UUID.randomUUID(), orderItems, fixedAmountVoucher.getVoucherId());

		//then : 행위 관점에서 생각을 해야한다.

		// note : 상태 검증
		assertThat(order.getTotalAmount(),is(100L));
		assertThat(order.getVoucher().isEmpty(),is(false));

		// note : 순서 검증
		InOrder inOrder = inOrder(voucherServiceMock); // voucher service만의 순서
		inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId()); //조회
		inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher); // 사용

		// note : 순서 검증2
		InOrder inOrder2 = inOrder(voucherServiceMock, orderRepositoryMock); // 함께 사용시 순서
		inOrder2.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId()); //조회
		inOrder2.verify(orderRepositoryMock).insert(order);
		inOrder2.verify(voucherServiceMock).useVoucher(fixedAmountVoucher); // 사용

		// note : 행위 검증 (orderservice 안에 있는 voucherservice, repository 행위 검증
		verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId());
		verify(orderRepositoryMock).insert(order);
		verify(voucherServiceMock).useVoucher(fixedAmountVoucher);

	}

	@DisplayName("")
	@Test
	void testCreateOrder() {
	}
}