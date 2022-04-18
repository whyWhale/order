package com.spring.order.domain.Voucher;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FixedAmountVoucherTest {
	private static final Logger log = LoggerFactory.getLogger(FixedAmountVoucherTest.class);

	@BeforeAll
	static void firstInit() {
		log.info("최초 한번 실행!");
	}

	@BeforeEach
	void setUp() {
		log.info("테스트 마다 실행!");
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getVoucherId() {
	}

	@Test
	void testDiscount() {
		// given
		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);

		// when
		// then
		assertEquals(900, fixedAmountVoucher.discount(1000));

	}

	@Test
	@DisplayName("할인 금액은 마이너스가 될수 없다.")
	@Disabled
	public void testWithMinus() {
		//given
		//when
		//then
		assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(UUID.randomUUID(), -100));
	}

	@Test
	@DisplayName("할인 금액이 원래 금액보다 큰 경우는 마이너스가 될수 없고 0원이 되어야 한다.")
	public void testMinusDiscountAmout() {
		//given
		FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(getUUID(), 1000);

		//when
		//then
		assertEquals(0, fixedAmountVoucher.discount(500));

	}

	@Test
	@DisplayName("유효한 할인 금액으로 생성할 수 있어야 한다.[백만원 이하]")
	public void testCreateVoucher() {
		//given
		//when
		//then
		assertAll("FixedAmountVoucher creation",
				() -> assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(getUUID(), -100)),
				() -> assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(getUUID(), 0)),
				() -> assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(getUUID(), 1000000000))
		);

	}

	public UUID getUUID() {
		return UUID.randomUUID();
	}
}