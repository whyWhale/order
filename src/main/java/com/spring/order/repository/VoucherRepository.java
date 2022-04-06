package com.spring.order.repository;

import java.util.Optional;
import java.util.UUID;

import com.spring.order.domain.Voucher.Voucher;

public interface VoucherRepository {
	Optional<Voucher> findById(UUID voucherId);

	Voucher insert(Voucher voucher);

}
