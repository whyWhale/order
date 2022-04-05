package com.spring.order.service;

import java.util.Optional;
import java.util.UUID;

import com.spring.order.domain.Voucher.Voucher;

public interface VoucherRepository {
	Optional<Voucher> findById(UUID voucherId);

}
