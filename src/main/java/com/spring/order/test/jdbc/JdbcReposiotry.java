package com.spring.order.test.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcReposiotry {
	private static final Logger log = LoggerFactory.getLogger(JdbcReposiotry.class);
	private static final String SELECT_BY_NAME = "select * from customers WHERE name = ? ";
	private static final String SELECT_ALL = "select * from customers";
	private static final String INSERT = "insert into customers(customer_id,name,email) values(UUID_TO_BIN(?),?,?)";
	private static final String UPDATE_NAME = "update customers set name = ? where customer_id = UUID_TO_BIN(?)";
	private static final String DELETE_ALL = "delete from customers";

	public static void main(String[] args) {
		/*
	    List<String> names = findName("tester00");
		names.forEach(name -> log.info("name : {} \n", name));

		// note : sql injection
		List<String> sqlInjection = findName("tester00 OR' 'a'='a");
		sqlInjection.forEach(name -> log.info("name : {}", name));

		// note : preparestatement
		List<String> namePreStatement = findNamePreStatement("tester00 OR' 'a'='a");
		namePreStatement.forEach(name -> log.info("name : {}", name));
		*/
	
		//note : delete all
		int count = deleteAll();
		log.info("delect count : {}", count);

		// note : insert
		UUID customerId = UUID.randomUUID();
		int result = insertCustomer(customerId, "kkk", "dasdad@gmail.com");
		if (result != 0) {
			List<String> all = findAll();
			all.forEach(val -> log.info("name is : {}", val));
		}

		List<UUID> allIds = findAllIds();
		allIds.forEach(val -> log.info("uuid : {}",val));

		int updateCount = updateCustomerName(customerId, "qqqqqweqweqwe");
		log.info("update count : {}",updateCount);
	}

	public static List<String> findName(String name) {
		String sql = "select * from customers where name = '%s'".formatted(name);
		List<String> names = new ArrayList<>();

		try (
				Connection connection = JdbcConfig.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);
		) {
			log.info("db connection success!");
			log.info("resultSet : {} ", resultSet);
			while (resultSet.next()) {
				UUID customer_id = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				String customerName = resultSet.getString("name");
				LocalDateTime created_at = resultSet.getTimestamp("created_at").toLocalDateTime();
				log.info("uuid column : {}, name column : {}, created_at : {}", customer_id, customerName, created_at);
				names.add(customerName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("DB connection error.....");
		}

		return names;
	}

	public static List<String> findNamePreStatement(String name) {

		List<String> names = new ArrayList<>();
		log.info("sql string : {}", SELECT_BY_NAME);

		try (
				Connection connection = JdbcConfig.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NAME);
		) {
			preparedStatement.setString(1, name);
			log.info("statement -> {} ", preparedStatement);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					UUID customer_id = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
					String customerName = resultSet.getString("name");
					LocalDateTime created_at = resultSet.getTimestamp("created_at").toLocalDateTime();
					log.info("uuid column : {}, name column : {}, created_at : {}", customer_id, customerName,
							created_at);
					names.add(customerName);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("DB connection error.....");
		}

		return names;
	}

	public static List<String> findAll() {

		List<String> names = new ArrayList<>();
		log.info("sql string : {}", SELECT_ALL);

		try (
				Connection connection = JdbcConfig.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL);
				ResultSet resultSet = preparedStatement.executeQuery()
		) {
			log.info("statement -> {} ", preparedStatement);

			while (resultSet.next()) {
				UUID customer_id = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				String customerName = resultSet.getString("name");
				LocalDateTime created_at = resultSet.getTimestamp("created_at").toLocalDateTime();
				log.info("uuid column : {}, name column : {}, created_at : {}", customer_id, customerName, created_at);
				names.add(customerName);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("DB connection error.....");
		}

		return names;
	}


	public static List<UUID> findAllIds() {

		List<UUID> cutomerIds = new ArrayList<>();
		log.info("sql string : {}", SELECT_ALL);

		try (
				Connection connection = JdbcConfig.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL);
				ResultSet resultSet = preparedStatement.executeQuery()
		) {
			log.info("statement -> {} ", preparedStatement);

			while (resultSet.next()) {
				UUID customer_id = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				String customerName = resultSet.getString("name");
				LocalDateTime created_at = resultSet.getTimestamp("created_at").toLocalDateTime();
				log.info("uuid column : {}, name column : {}, created_at : {}", customer_id, customerName, created_at);
				cutomerIds.add(customer_id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("DB connection error.....");
		}

		return cutomerIds;
	}

	public static int insertCustomer(UUID customerId, String name, String email) {
		try (Connection connection = JdbcConfig.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(INSERT);) {
			preparedStatement.setString(1, customerId.toString());
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, email);

			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static int updateCustomerName(UUID customerId, String newName) {
		try (Connection connection = JdbcConfig.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_NAME);) {
			preparedStatement.setString(1, newName);
			preparedStatement.setString(2, customerId.toString());

			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static int deleteAll() {
		try (Connection connection = JdbcConfig.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL);) {

			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}
}
