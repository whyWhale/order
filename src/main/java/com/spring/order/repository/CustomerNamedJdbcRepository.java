package com.spring.order.repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.spring.order.domain.Customer;

@Repository
public class CustomerNamedJdbcRepository implements CustomerRepository {
	final Logger log = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final PlatformTransactionManager transactionManager;

	private final TransactionTemplate transactionTemplate;
	private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER = (rs, rowNum) -> {
		UUID id = toUUID(rs.getBytes("customer_id"));
		String name = rs.getString("name");
		String email = rs.getString("email");
		LocalDateTime lastLoginAt = rs.getTimestamp("last_login_at")
				== null ? null : rs.getTimestamp("last_login_at")
				.toLocalDateTime();
		LocalDateTime createdAt = rs.getTimestamp(("created_at")).toLocalDateTime();

		return new Customer(id, name, email, lastLoginAt, createdAt);
	};

	public CustomerNamedJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate,
			PlatformTransactionManager transactionManager, TransactionTemplate transactionTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.transactionManager = transactionManager;
		this.transactionTemplate = transactionTemplate;
	}

	private Map<String, Object> toParameters(Customer customer) {
		return new HashMap<>() {{
			put("customerId", customer.getId().toString().getBytes());
			put("name", customer.getName());
			put("email", customer.getEmail());
			put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
			put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
		}};
	}

	@Override
	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from customers",
				Collections.EMPTY_MAP,
				Integer.class);
	}

	@Override
	public Customer insert(Customer customer) {
		int update = jdbcTemplate.update(
				"INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(:customerId), :name , :email, :createdAt)",
				toParameters(customer));

		if (update != 1) {
			throw new RuntimeException();
		}

		return customer;
	}

	@Override
	public Customer update(Customer customer) {
		int result = jdbcTemplate.update(
				"UPDATE customers SET name = :name, email = :email, last_login_at = :lastLoginAt WHERE customer_id = UUID_TO_BIN(:customerId)",
				toParameters(customer)
		);

		if (result != 1) {
			throw new RuntimeException();
		}
		return customer;
	}

	@Override
	public List<Customer> findAll() {
		return jdbcTemplate.query("select * from customers", CUSTOMER_ROW_MAPPER);
	}

	@Override
	public Optional<Customer> findById(UUID customerId) {

		try {

			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							"select * from customers where customer_id = UUID_TO_BIN( :customerId )",
							Collections.singletonMap("customerId", customerId.toString().getBytes()),
							CUSTOMER_ROW_MAPPER)
			);

		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		} catch (IncorrectResultSizeDataAccessException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	@Override
	public Optional<Customer> findByName(String name) {

		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject("SELECT * FROM customers WHERE name =:name",
							Collections.singletonMap("name", name),
							CUSTOMER_ROW_MAPPER)
			);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE email = :email",
					Collections.singletonMap("email", email),
					CUSTOMER_ROW_MAPPER));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public void deleteAll() {
		jdbcTemplate.update("DELETE FROM customers", Collections.emptyMap());
	}

	// todo : 트랜잭션 템플릿이 또 따로 있음.
	public void transaction(Customer customer) {
		TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = UUID_TO_BIN(:customerId)",
					this.toParameters(customer));
			jdbcTemplate.update("UPDATE customers SET email = :email WHERE customer_id = UUID_TO_BIN(:customerId)",
					this.toParameters(customer));
			// note : commit
			transactionManager.commit(transaction);
		} catch (DataAccessException e) {
			log.error("error : {},  rollback 🤟", e.toString());

			// note : rollback
			transactionManager.rollback(transaction);
		}
	}

	public void transactionTemplate(Customer customer){
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			// note : try 슬 필요 없고 알아서 롤백해줌...!!
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = UUID_TO_BIN(:customerId)",
						toParameters(customer));
				jdbcTemplate.update("UPDATE customers SET email = :email WHERE customer_id = UUID_TO_BIN(:customerId)",
						 toParameters(customer));
			}
		});
	}

	public static UUID toUUID(byte[] bytes) {
		ByteBuffer wrap = ByteBuffer.wrap(bytes);
		return new UUID(wrap.getLong(), wrap.getLong());
	}
}
