package com.spring.order.domain.Voucher;

import java.util.UUID;

public class PercentDiscountVoucher implements Voucher {
	private final UUID voucherId;
	private final long percent;

	public PercentDiscountVoucher(UUID voucherId, long percent) {
		this.voucherId = voucherId;
		this.percent = percent;
	}

	@Override
	public UUID getVoucherId() {
		return voucherId;
	}

	public long discount(long beforeDiscount) {
		System.out.println();
		return beforeDiscount - (percent / 100) * 100;
	}
}
