package com.spring.order.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.spring.order.domain.Customer;

public class CustomerRowJdbcRepository {

	private static final Logger logger = LoggerFactory.getLogger(CustomerRowJdbcRepository.class);
	private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";
	private final String SELECT_ALL_SQL = "select * from customers";
	private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?), ?, ?)";
	private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
	private final String DELETE_ALL_SQL = "DELETE FROM customers";

	private String url = "jdbc:mysql://localhost/kdt_order";
	private String user = "root";
	private String password = "";

	public List<String> findNames(String name) {
		List<String> names = new ArrayList<>();
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
		) {
			statement.setString(1, name);
			logger.info("statement -> {}", statement);
			try (var resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					var customerName = resultSet.getString("name");
					var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
					var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
					names.add(customerName);
				}
			}
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}

		return names;
	}

	public List<String> findAllName() {
		List<String> names = new ArrayList<>();
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(SELECT_ALL_SQL);
				var resultSet = statement.executeQuery()
		) {
			while (resultSet.next()) {
				var customerName = resultSet.getString("name");
				var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
				names.add(customerName);
			}
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}

		return names;
	}

	public List<UUID> findAllIds() {
		List<UUID> uuids = new ArrayList<>();
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(SELECT_ALL_SQL);
				var resultSet = statement.executeQuery()
		) {
			while (resultSet.next()) {
				var customerName = resultSet.getString("name");
				var customerId = toUUID(resultSet.getBytes("customer_id"));
				var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
				uuids.add(customerId);
			}
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}

		return uuids;
	}

	public int insertCustomer(UUID customerId, String name, String email) {
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(INSERT_SQL);
		) {
			statement.setBytes(1, customerId.toString().getBytes());
			statement.setString(2, name);
			statement.setString(3, email);
			return statement.executeUpdate();
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}
		return 0;
	}

	public int updateCustomerName(UUID customerId, String name) {
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
		) {
			statement.setString(1, name);
			statement.setBytes(2, customerId.toString().getBytes());
			return statement.executeUpdate();
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}
		return 0;
	}

	public int deleteAllCustomers() {
		try (
				var connection = DriverManager.getConnection(url, user, password);
				var statement = connection.prepareStatement(DELETE_ALL_SQL);
		) {
			return statement.executeUpdate();
		} catch (SQLException throwable) {
			logger.error("Got error while closing connection", throwable);
		}
		return 0;
	}

	public void transactionTest(Customer customer) {
		String updateNameSql = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
		String updateEmailSql = "UPDATE customers SET email = ? WHERE customer_id = UUID_TO_BIN(?)";

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
			try (
					var updateNameStatement = connection.prepareStatement(updateNameSql);
					var updateEmailStatement = connection.prepareStatement(updateEmailSql);
			) {
				connection.setAutoCommit(false); // NOTE :트랙잭션 하나로 묶기
				updateNameStatement.setString(1, customer.getName());
				updateNameStatement.setBytes(2, customer.getId().toString().getBytes());
				updateNameStatement.executeUpdate();

				updateEmailStatement.setString(1, customer.getEmail());
				updateEmailStatement.setBytes(2, customer.getId().toString().getBytes());
				updateEmailStatement.executeUpdate();
				connection.setAutoCommit(true); // NOTE :트랙잭션 하나로 묶기
			}
		} catch (SQLException exception) {
			if (connection != null) {
				try {
					connection.rollback();
					connection.close();
				} catch (SQLException throwable) {
					logger.error("Got error while closing connection", throwable);
					throw new RuntimeException(exception);
				}
			}
			logger.error("Got error while closing connection", exception);
			throw new RuntimeException(exception);
		}

	}

	static UUID toUUID(byte[] bytes) {
		var byteBuffer = ByteBuffer.wrap(bytes);
		return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
	}

	public static void main(String[] args) {
		var customerRepository = new CustomerRowJdbcRepository();

		customerRepository.transactionTest(
				new Customer(UUID.fromString("f6a845d4-c376-11ec-9009-3cdb955b0816"), "updateTester",
						"test01@gmail.com", LocalDateTime.now()));

		//    var count = customerRepository.deleteAllCustomers();
		//    logger.info("deleted count -> {}", count);
		//
		//    var customerId = UUID.randomUUID();
		//    logger.info("created customerId -> {}", customerId);
		//    logger.info("created UUID Version -> {}", customerId.version());

		//    customerRepository.insertCustomer(customerId, "new-user2", "new-user2@gmail.com");
		//    customerRepository.findAllIds().forEach(v -> logger.info("Found customerId : {} and version : {}", v, v.version()));
	}
}
