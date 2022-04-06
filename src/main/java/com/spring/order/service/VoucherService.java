package com.spring.order.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.spring.order.domain.Voucher.Voucher;
import com.spring.order.repository.VoucherRepository;

@Service
public class VoucherService {
	private final VoucherRepository voucherRepository;

	public VoucherService(VoucherRepository voucherRepository) {
		this.voucherRepository = voucherRepository;
	}

	public Voucher getVoucher(UUID voucherId) {

		return voucherRepository.findById(voucherId)
				.orElseThrow(() -> new RuntimeException("Not Found Resource"));
	}

	public void useVoucher(Voucher voucher) {

	}
}
