package com.spring.order.domain.Voucher;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HamcrestAssertionTest {

	@Test
	@DisplayName("여러 hamcrest matcher test")
	public void hamcrestTest() {
		// jupiter
		assertEquals(1, 1);

		// hamcrest
		assertThat(11, equalTo(10 + 1));
		assertThat(11, is(10 + 1));
		assertThat(11, anyOf(is(10 + 1), is(12)));

		assertNotEquals(1, 1 + 1);
		assertThat(1 + 1, not(equalTo(3)));
	}

	@Test
	@DisplayName("컬렉션 matcher test")
	public void collectionTest() {
		//given
		var prices = List.of(1, 2, 3, 4, 5);
		//when
		//then
		assertThat(prices, hasSize(5));
		assertThat(prices,everyItem(greaterThan(0)));
		assertThat(prices,containsInAnyOrder(1,3,2,4,5)); // 순서 비의존
		assertThat(prices,contains(1,2,3,4,5)); // 순서 의존
		assertThat(prices,hasItem(greaterThan(4)));
	}

}
