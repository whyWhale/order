package com.spring.order.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.spring.order.domain.Voucher.Voucher;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoryVoucherRepository implements VoucherRepository {
	static final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

	@Override
	public Optional<Voucher> findById(UUID voucherId) {
		return Optional.ofNullable(storage.get(voucherId));
	}

	@Override
	public Voucher insert(Voucher voucher) {
		storage.put(voucher.getVoucherId(), voucher);
		return voucher;
	}
}
