package com.spring.order.repository;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.spring.order.domain.Customer;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {

	public static final String NOT_INSERT_ERROR = "not insert ....";
	public static final String NOTING_WAS_UPDATED = "Noting was updated";
	public static final String ERROR_NO_DATA = "error : no data";
	public static final String ERROR_GRATER_THAN_1_SIZE = "error : grater than 1 size ";
	public static final String GOT_EMPTY_RESULT = "Got empty result";
	private static final Logger log = LoggerFactory.getLogger(CustomerJdbcRepository.class);

	private final DataSource dataSource;
	private final JdbcTemplate jdbcTemplate;
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

	public CustomerJdbcRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
	}

	@Override
	public Customer insert(Customer customer) {
		int update = jdbcTemplate.update(
				"INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(?), ?, ?, ?)",
				customer.getId().toString().getBytes(),
				customer.getName(),
				customer.getEmail(),
				Timestamp.valueOf(customer.getCreatedAt()));
		if (update != 1) {
			throw new RuntimeException(NOT_INSERT_ERROR);
		}

		return customer;
	}

	@Override
	public Customer update(Customer customer) {
		int result = jdbcTemplate.update(
				"UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)",
				customer.getName(),
				customer.getEmail(),
				customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null,
				customer.getId().toString().getBytes()
		);
		if (result != 1) {
			throw new RuntimeException(NOTING_WAS_UPDATED);
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
					jdbcTemplate.queryForObject("select * from customers where customer_id = UUID_TO_BIN( ? )",
							CUSTOMER_ROW_MAPPER,
							customerId.toString().getBytes())
			);

		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			log.error(ERROR_NO_DATA);
		} catch (IncorrectResultSizeDataAccessException e) {
			e.printStackTrace();
			log.error(ERROR_GRATER_THAN_1_SIZE);
		}

		return Optional.empty();
	}

	@Override
	public Optional<Customer> findByName(String name) {

		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject("SELECT * FROM customers WHERE name =?",
							CUSTOMER_ROW_MAPPER,
							name)
			);
		} catch (EmptyResultDataAccessException e) {
			log.error(GOT_EMPTY_RESULT, e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE email = ?",
					CUSTOMER_ROW_MAPPER,
					email));
		} catch (EmptyResultDataAccessException e) {
			log.error(GOT_EMPTY_RESULT, e);
			return Optional.empty();
		}
	}

	@Override
	public void deleteAll() {
		jdbcTemplate.update("DELETE FROM customers");
	}

	public void transactionTest(Customer customer) {
		String updatedNameSql = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
		String updatedEmailSql = "UPDATE customers SET email = ? WHERE customer_id = UUID_TO_BIN(?)";

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
			connection.setAutoCommit(false);
			try (
					var updateNameStatement = connection.prepareStatement(updatedNameSql);
					var updateEmailStatement = connection.prepareStatement(updatedEmailSql);
			) {
				connection.setAutoCommit(false);
				updateNameStatement.setString(1, customer.getName());
				updateNameStatement.setBytes(2, customer.getId().toString().getBytes());
				updateNameStatement.executeUpdate();

				updateEmailStatement.setString(1, customer.getEmail());
				updateEmailStatement.setBytes(2, customer.getId().toString().getBytes());
				updateEmailStatement.executeUpdate();
				connection.setAutoCommit(true);
			}
		} catch (SQLException exception) {
			if (connection != null) {
				try {
					connection.rollback();
					connection.close();
				} catch (SQLException throwable) {
					throw new RuntimeException(exception);
				}
			}
			throw new RuntimeException(exception);
		}
	}


	public static UUID toUUID(byte[] bytes) {
		ByteBuffer wrap = ByteBuffer.wrap(bytes);
		return new UUID(wrap.getLong(), wrap.getLong());
	}
}
