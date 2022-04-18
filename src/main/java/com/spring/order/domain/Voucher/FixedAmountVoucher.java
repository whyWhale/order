package com.spring.order.domain.Voucher;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher {
	private final static int MIN_AMOUNT=0;
	private final static int MAX_AMOUNT=1_000_000;

	private final UUID voucherId;
	private final long amount;

	public FixedAmountVoucher(UUID voucherId, long amount) {
		if (amount <= MIN_AMOUNT || amount>= MAX_AMOUNT) {
			throw new IllegalArgumentException("Amount should be positive !");
		}

		this.voucherId = voucherId;
		this.amount = amount;
	}

	@Override
	public UUID getVoucherId() {
		return voucherId;
	}

	public long discount(long beforeDiscount) {
		long discount = beforeDiscount - amount;

		return discount <0 ? 0L : discount ;
	}

}
